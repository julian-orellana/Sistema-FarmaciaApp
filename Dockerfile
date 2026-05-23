# ─── Stage 1: compilar ────────────────────────────────────────────────────────
FROM gradle:8-jdk17-alpine AS build
WORKDIR /app
COPY build.gradle .
COPY settings.gradle .
COPY src ./src
# -x test: no correr tests en build de imagen (se corren en CI antes)
RUN gradle build -x test --no-daemon

# ─── Stage 2: imagen de ejecución ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Usuario no-root para no correr como root dentro del contenedor
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# JAVA_OPTS se inyecta desde docker-compose como variable de entorno
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]