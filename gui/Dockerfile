FROM node:22 as build-stage
WORKDIR /app
COPY package.json ./
COPY yarn.lock ./
RUN yarn 
COPY ./ .
RUN yarn run build

FROM nginx:alpine-slim as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app
COPY nginx.conf /etc/nginx/nginx.conf
