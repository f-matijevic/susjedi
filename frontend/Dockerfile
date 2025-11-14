FROM node:25-alpine AS build
WORKDIR /builder

ARG VITE_BASE_URL
ARG NODE_ENV=production

COPY package*.json ./
RUN npm ci

COPY . ./
RUN npm run build

FROM caddy:2-alpine

ENV PORT=8080
EXPOSE 8080

COPY Caddyfile /etc/caddy/Caddyfile
COPY --from=build /builder/dist /srv
