# =============================================================
# Mall-X 微服务通用 Dockerfile（JDK17 运行期）
# 用法（以 mall-auth-service 为例）：
#   mvn -pl mall-auth-service -am clean package -DskipTests
#   docker build -t mall/mall-auth-service:latest \
#     -f docker/Dockerfile --build-arg JAR=mall-backend/mall-auth-service/target/mall-auth-service-0.1.0-SNAPSHOT.jar .
# =============================================================
FROM eclipse-temurin:17-jre
ARG JAR
WORKDIR /app
COPY ${JAR} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
