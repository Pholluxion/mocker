# Etapa 1: Construcción de la aplicación Angular
FROM node:18 AS build

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar los archivos de dependencias y ejecutamos npm install
COPY package.json package-lock.json ./
RUN npm install

# Copiar todo el código fuente
COPY . .

# Construir la aplicación Angular
RUN npm run build --prod

# Etapa 2: Servir la aplicación con Nginx
FROM nginx:1.23

# Copiar el build de Angular al directorio de Nginx
COPY --from=build /app/dist/demo/browser /usr/share/nginx/html

# Copiar el archivo de configuración de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer el puerto 80
EXPOSE 80

# Iniciar Nginx
CMD ["nginx", "-g", "daemon off;"]
