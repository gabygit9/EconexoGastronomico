#build app
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json ./
RUN npm config delete registry && \
    npm config set registry https://registry.npmjs.org/ && \
    npm install
COPY . .
RUN npm run build -- --configuration production

#nginx para servir los estáticos
FROM nginx:alpine
COPY --from=build /app/dist/econexo/browser /usr/share/nginx/html
COPY nginx.config /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
