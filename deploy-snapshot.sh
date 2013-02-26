#!/bin/sh

mvn clean package javadoc:jar source:jar deploy
