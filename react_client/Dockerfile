FROM node:latest as build

WORKDIR /secure-markdown-frontend

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build /secure-markdown-frontend/build/ /etc/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
EXPOSE 3000