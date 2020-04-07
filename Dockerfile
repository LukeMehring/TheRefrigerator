FROM adoptopenjdk:11-jre-hotspot

ADD build/distributions/TheRefrigerator.tar /

ENTRYPOINT ["/TheRefrigerator/bin/TheRefrigerator"]
