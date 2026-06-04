FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 安装 Gradle
RUN apk add --no-cache curl unzip \
    && curl -L https://services.gradle.org/distributions/gradle-7.6-bin.zip -o gradle.zip \
    && unzip gradle.zip \
    && rm gradle.zip \
    && mv gradle-7.6 /opt/gradle

ENV PATH="/opt/gradle/bin:${PATH}"

# 复制源代码
COPY build.gradle settings.gradle ./
COPY src ./src

# 构建应用
RUN gradle bootJar --no-daemon

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=0 /app/build/libs/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/api/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
