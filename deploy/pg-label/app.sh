#!/bin/bash
set +m -o pipefail
shopt -s lastpipe

log() {
    printf '%s\n' "$1" >&2
}

events() {
    docker events \
        -f "type=container" \
        -f "label=pg-label.enable" \
        -f "event=create" -f "event=destroy" \
        --format "{{.Action}},{{.Actor.ID}},{{json .Actor.Attributes}}"
}

labels() {
    FMT='to_entries[] | "\(.key)=\(.value)"'
    jq -r "$FMT" <<< "$1"
}

sql_do() {
    echo 'DO $$'; cat; echo '$$;'
}

on_create() {
    sql_do <<EOF
DECLARE
  _pg_name TEXT := quote_ident('$PG_NAME');
  _pg_user TEXT := quote_ident('$PG_USER');
  _pg_pass TEXT := quote_literal('$PG_PASS');
  _persist BOOLEAN := '$PERSIST';
BEGIN
  RAISE NOTICE '$NAME: Stvaram bazu % za korisnika % (trajno: %)',
    _pg_name, _pg_user, _persist;
  BEGIN
    PERFORM dblink_exec('', 'CREATE DATABASE ' || _pg_name) || ';';
    EXCEPTION WHEN duplicate_database THEN
      IF _persist THEN
        RAISE NOTICE 'Zadana baza vec postoji';
      ELSE
        RAISE EXCEPTION 'Postojeca baza ne smije biti privremena';
      END IF;
  END;
  BEGIN
    EXECUTE 'CREATE USER ' || _pg_user || ' WITH PASSWORD ' || _pg_pass || ';';
    EXECUTE 'GRANT ALL PRIVILEGES ON DATABASE ' || _pg_name || ' TO ' || _pg_user || ';';
    EXCEPTION WHEN duplicate_object THEN
      IF _persist THEN
        RAISE NOTICE 'Zadani korisnik vec postoji';
      ELSE
        RAISE EXCEPTION 'Postojeci korisnik ne smije biti privremen';
      END IF;
  END;
END;
EOF
}

on_destroy() {
    sql_do <<EOF
DECLARE
  _pg_name TEXT := quote_ident('$PG_NAME');
  _pg_user TEXT := quote_ident('$PG_USER');
  _persist BOOLEAN := '$PERSIST';
BEGIN
  IF NOT _persist THEN
    RAISE NOTICE '$NAME: Odbacujem privremenu bazu % i korisnika %',
      _pg_name, _pg_user;
    PERFORM dblink_exec('', 'DROP DATABASE IF EXISTS ' || _pg_name) || ';';
    EXECUTE 'DROP USER IF EXISTS ' || _pg_user || ';';
  END IF;
END;
EOF
}

main() {
    echo "CREATE EXTENSION IF NOT EXISTS dblink;"
    log "pg-label: spreman"

    while IFS=',' read -r EVENT ID ATTR; do
        local NAME PG_NAME="" PG_USER="" PG_PASS="" PERSIST=true

        labels "$ATTR" | while IFS='=' read -r KEY VAL; do
            case "$KEY" in
                name) NAME="$VAL";;
                pg-label.database) PG_NAME="$VAL";;
                pg-label.username) PG_USER="$VAL";;
                pg-label.password) PG_PASS="$VAL";;
                pg-label.ephemeral) PERSIST=false;;
            esac
        done

        log "$EVENT: $NAME ($ID)"

        if [ -z "$PG_NAME$PG_USER" ]; then
            if [ "$PERSIST" == "true" ]; then
                log "greska: $NAME: Za trajnu bazu nije zadano barem ime ili korisnicko ime"
                continue
            fi
            PG_USER="$ID"
        fi

        PG_NAME="${PG_NAME:-$PG_USER}"
        PG_USER="${PG_USER:-$PG_NAME}"
        PG_PASS="${PG_PASS:-$PG_USER}"

        case "$EVENT" in
            create) on_create;;
            destroy) on_destroy;;
        esac
    done
}

DRY_RUN=${DRY_RUN+cat}
export PGAPPNAME=pg-label

events | main | ${DRY_RUN:-psql -Antw}
