#!/bin/bash

# ES_HOME required to detect elasticsearch jars

export JDBC_IMPORTER_HOME=/home/ubuntu/feeder/elasticsearch-jdbc-1.7.1.0
export ES_HOME=/home/ubuntu/feeder/elasticsearch-jdbc-1.7.1.0
bin=$JDBC_IMPORTER_HOME/bin
lib=$JDBC_IMPORTER_HOME/lib

echo '
{
"type": "jdbc",
"jdbc": {
"schedule" : "0 0/5 * * * ?",
"url": "jdbc:postgresql://52.91.128.2:5432/egov_uat_db",
"user": "postgres",
"password": "eG0v@p$ql7918",
"strategy": "standard",
"timezone": "localtime",
"sql": "SELECT UPICNO as \"_id\" ,city.name as \"cityname\", city.districtname as \"districtname\", city.regionname as \"regionname\", UPICNO as \"assessmentno\", OWNERSNAME \"ownername\", mv.mobileno \"mobileno\", ADDRESS as \"address\", ptype.PROPERTY_TYPE \"property_type\", zone.name AS \"revzonename\", ward.name AS \"revwardname\", eleward.name as  \"electwardname \", mv.SITAL_AREA AS  \"sitalarea\", mv.TOTAL_BUILTUP_AREA AS  \"builduparea\", mv.AGGREGATE_CURRENT_DEMAND as \"currentdemand\", mv.AGGREGATE_ARREAR_DEMAND as\"arreardemand\", mv.CURRENT_COLLECTION \"currentcollection\", mv.ARREARCOLLECTION AS \"arrearcollection\", mv.ALV AS \"arv_amount\",(mv.AGGREGATE_CURRENT_DEMAND+mv.AGGREGATE_ARREAR_DEMAND) AS \"totaldemand\", (mv.CURRENT_COLLECTION+mv.ARREARCOLLECTION) AS \"totalcollection\", (mv.AGGREGATE_CURRENT_DEMAND+mv.AGGREGATE_ARREAR_DEMAND)-(mv.CURRENT_COLLECTION+mv.ARREARCOLLECTION) AS  \"totalblance\" , mv.AGGREGATE_CURRENT_DEMAND AS  \"currentyeardemand\",((mv.AGGREGATE_ARREAR_DEMAND-mv.AGGREGATE_CURRENT_DEMAND)-(mv.CURRENT_COLLECTION+mv.ARREARCOLLECTION)) AS \"arrearbalance\", (mv.AGGREGATE_CURRENT_DEMAND-mv.CURRENT_COLLECTION) AS \"totalbalance\",mv.usage as \"usage\" FROM :schemaName.egpt_mv_propertyinfo mv , :schemaName.egpt_property_type_master ptype, :schemaName.eg_boundary zone, :schemaName.eg_boundary ward,:schemaName.eg_boundary eleward, :schemaName.eg_city city WHERE ptype.ID=mv.PROPTYMASTER AND mv.zoneid=zone.id AND mv.wardid=ward.id and mv.electionwardid=eleward.id",
"index": "apptisdetailed",
"type": "apptcollectiondetails",
"elasticsearch" : {
     "cluster" : "elasticsearch_ap_uat",
     "host" : "localhost",
     "port" : 9300
}
    }   
}' | java \
-cp "${lib}/*" \
-Dlog4j.configurationFile=${bin}/log4j2.xml \
org.xbib.tools.Runner \
org.xbib.tools.JDBCImporter
