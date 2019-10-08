FROM 172.31.26.137:5000/paascloud/base:v1

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""
ENV PARAMS=""
ADD target/*.jar app.jar
CMD ${PARAMS}
CMD echo "The application will start " && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar ${PARAMS}

EXPOSE 9002
