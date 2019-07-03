#!/bin/sh

export CLASSPATH=
for jar in `ls lib/*.jar`
do
  export CLASSPATH=$CLASSPATH:$jar
done
export CLASSPATH=$CLASSPATH

nohup java -Djsse.enableSNIExtension=false -Dsun.net.inetaddr.ttl=0 -Xmx4G -cp test_etranslation_resp_calc:classes:$CLASSPATH eu.europeana.research.etranslation.test.TestEtranslationClient data/europeana-portal-query-sample_requests /home/nfreire/etranslation/translations-repository $1 $2 $3 $4  >output-ScriptTestEtranslationClientCallbackResponses.txt 2>&1 &
