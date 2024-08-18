FROM node:18-alpine

RUN apk add --no-cache openjdk11

WORKDIR /usr/src/app
COPY client/ ./client
COPY docs/ ./docs

WORKDIR /usr/src/app/client
RUN npm install
RUN npm run generate:with-api

EXPOSE 3000

CMD ["npm", "start"]
