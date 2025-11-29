package engine.tests;


import org.junit.Test;


public class JavaToStringParser {

    @Test
    public void testParse() {
        Item item = Item.parseString("[[{propertyName='TOTAL_SOURCE_XDOCS', propertyValue='2032'}, {propertyName='FALLBACK_RESULT_FETCHER_STATE', propertyValue='{\"indentifiersQueue\":[],\"currentEventType\":\"PREDECESSOR_TASK_PARTIALLY_FINISHED\",\"lastFetchedEddId\":\"\",\"totalSourceCount\":{\"value\":0},\"totalSourceProcessedSuccessfully\":{\"value\":1995},\"totalProfiledTaskCount\":{\"value\":2000}}'}, {propertyName='FALLBACK_EXDOC_CURSOR', propertyValue='UDW##VLNRDW01#UDW_ADM#T_QUALIFIER_191028_0004141'}, {propertyName='ProfileExecutorTaskStatus', propertyValue='COMPLETED'}, {propertyName='FALLBACK_PROFILE_EXECUTION_STATE', propertyValue='{\"eddIdToStatusMap\":{},\"failedProfileTaskCount\":4,\"fetchedSourceCount\":2000,\"submittedProfileTaskCount\":2000,\"executedProfileTaskCount\":2000,\"failedTableStatIndex\":1,\"lastEddFinishedTime\":0,\"identifierToResultFileMap\":{}}'}, {propertyName='RESULT_FETCHER_STATE', propertyValue='{\"indentifiersQueue\":[],\"currentEventType\":\"PREDECESSOR_TASK_PARTIALLY_FINISHED\",\"lastFetchedEddId\":\"U:XwC01-3-EeuXV0MqMyfdtw\",\"totalSourceCount\":{\"value\":2032},\"totalSourceProcessedSuccessfully\":{\"value\":1995},\"totalProfiledTaskCount\":{\"value\":2000}}'}, {propertyName='PROFILE_EXECUTION_STATE', propertyValue='{\"eddIdToStatusMap\":{},\"failedProfileTaskCount\":4,\"fetchedSourceCount\":2032,\"submittedProfileTaskCount\":2032,\"executedProfileTaskCount\":2032,\"failedTableStatIndex\":5,\"lastEddFinishedTime\":1627310677399,\"identifierToResultFileMap\":{}}'}, {propertyName='INSERT_TIME', propertyValue='07/26/2021 18:53:22'}, {propertyName='SimValidationOutcome', propertyValue='SUCCESS'}, {propertyName='FALLBACK_STATE_CACHE', propertyValue='{\"pesCursor\":[],\"pesInfo\":[],\"rfsCursor\":[],\"rfsPesInfo\":[]}'}]]");
        System.out.println(item);
    }

}
