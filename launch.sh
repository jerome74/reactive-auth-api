#!/bin/bash

mvn clean package
docker build -t reactor-api .