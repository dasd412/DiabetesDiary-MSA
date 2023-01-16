# stage 1 : openJDK 이미지에서 build라고 명명된 이미지 생성 후 압축 해제
FROM openjdk:11-slim as build

LABEL maintainer="Young Jun Yang <dasd412@naver.com>"

ARG JAR_FILE

COPY ${JAR_FILE} app.jar

# jar 파일 압축 풀기
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)


# stage 2 : 전체 jar 파일 대신, 레이어만 포함된 또 다른 이미지 생성
FROM openjdk:11-slim

VOLUME /tmp

# stage 1에서 build라고 명명된 첫 이미지에서 여러 레이어 복사.
ARG DEPENDENCY=/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","com.dasd412.api.diaryservice.DiaryServiceApplication"]