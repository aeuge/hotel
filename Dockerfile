FROM maven:3.6.1-jdk-12

WORKDIR /app

ADD ./pom.xml /app
RUN mvn dependency:resolve

ADD ./src/ /app/src
RUN mvn install

FROM openjdk:12.0.2-jdk-oracle

RUN curl -O  https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    yum install google-chrome-stable_current_x86_64.rpm -y && \
    yum install unzip -y && \
    mkdir /opt/chrome && \
    curl -O https://chromedriver.storage.googleapis.com/2.41/chromedriver_linux64.zip && \
    unzip chromedriver_linux64.zip -d /opt/chrome

WORKDIR /app

COPY --from=0 /app/target/ /app

EXPOSE 8080
EXPOSE 27017

CMD ["java","-jar","hotel-1.01.jar"] 
#CMD ["ls"] 