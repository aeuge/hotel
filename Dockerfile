FROM maven:3.6.1-jdk-12

WORKDIR /app

ADD ./pom.xml /app
RUN mvn dependency:resolve

ADD ./src/ /app/src
RUN mvn install

FROM openjdk:12.0.2-jdk-oracle

RUN apt-get update
RUN apt-get install -y gnupg wget curl unzip --no-install-recommends && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list && \
    apt-get update -y && \
    apt-get install -y google-chrome-stable && \
    CHROMEVER=$(google-chrome --product-version | grep -o "[^\.]*\.[^\.]*\.[^\.]*") && \
    DRIVERVER=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROMEVER") && \
    wget -q --continue -P /chromedriver "http://chromedriver.storage.googleapis.com/$DRIVERVER/chromedriver_linux64.zip" && \
    unzip /chromedriver/chromedriver* -d /chromedriver

WORKDIR /app

COPY --from=0 /app/target/ /app

EXPOSE 8080
EXPOSE 27017

CMD ["java","-jar","hotel-1.01.jar"] 
#CMD ["ls"] 