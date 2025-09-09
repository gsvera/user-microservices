#!/bin/sh

# Espera a que PostgreSQL esté listo
/wait-for-it.sh db:5432 --timeout=60 --strict -- echo "PostgreSQL está listo"

# Espera a que Eureka esté listo
/wait-for-it.sh services:8761 --timeout=60 --strict -- echo "Eureka está listo"

# Luego inicia el microservicio
exec java -jar /app/app.jar
