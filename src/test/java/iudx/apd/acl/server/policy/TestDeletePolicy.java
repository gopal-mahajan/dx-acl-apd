package iudx.apd.acl.server.policy;

import static iudx.apd.acl.server.Utility.INSERT_INTO_POLICY_TABLE;
import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.policy.util.Constants.DELETE_POLICY_QUERY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Tuple;
import iudx.apd.acl.server.Utility;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.common.ResponseUrn;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith({MockitoExtension.class, VertxExtension.class})
public class TestDeletePolicy {
    private static final Logger LOG = LoggerFactory.getLogger(TestDeletePolicy.class);
    @Container
    static PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:12.11");
    private static DeletePolicy deletePolicy;
    private static Utility utility;
    private static User owner;
    private JsonArray policyList;

    @BeforeAll
    public static void setUp(VertxTestContext vertxTestContext)
    {
        utility = new Utility();
        PostgresService pgService = utility.setUp(container);

        utility.testInsert().onComplete(handler -> {
            if (handler.succeeded())
            {
                owner = getOwner();
                deletePolicy = new DeletePolicy(pgService);
                vertxTestContext.completeNow();
            }
            else
            {
                vertxTestContext.failNow("Failed to set up");
            }
        });
    }

    public static User getOwner()
    {
        JsonObject jsonObject = new JsonObject()
                .put("userId", utility.getOwnerId())
                .put("userRole", "provider")
                .put("emailId", utility.getOwnerEmailId())
                .put("firstName", utility.getOwnerFirstName())
                .put("lastName", utility.getOwnerLastName());
        return new User(jsonObject);
    }
    @Test
    @DisplayName("Test initiateDeletePolicy : Success")
    public void testInitiateDeletePolicy(VertxTestContext vertxTestContext)
    {
        policyList = new JsonArray();
        policyList.add(new JsonObject().put("id", utility.getPolicyId()));

        deletePolicy.initiateDeletePolicy(policyList, owner).onComplete(handler -> {
            if (handler.succeeded())
            {
                assertEquals(ResponseUrn.SUCCESS_URN.getUrn(),handler.result().getString(TYPE));
                assertEquals(ResponseUrn.SUCCESS_URN.getMessage(), handler.result().getString(TITLE));
                assertEquals("Policy deleted successfully", handler.result().getString(RESULT));
                vertxTestContext.completeNow();

            }
            else
            {
                vertxTestContext.failNow("Failed");
            }
        });
    }

    @Test
    @DisplayName("Test initiateDeletePolicy with an invalid policy id list")
    public void testInitiateDeletePolicy4InvalidPolicy(VertxTestContext vertxTestContext)
    {
        policyList = new JsonArray();
        policyList.add(new JsonObject().put("id", utility.getPolicyId()));
        policyList.add(new JsonObject().put("id", utility.getOwnerId()));

        deletePolicy
                .initiateDeletePolicy(policyList, owner)
                .onComplete(
                        handler -> {
                            if (handler.succeeded()) {
                                vertxTestContext.failNow("Succeeded with an invalid policy in policy list");
                            } else {
                                JsonObject result =  new JsonObject(handler.cause().getMessage());
                                assertEquals(400, result.getInteger(TYPE));
                                assertEquals(ResponseUrn.BAD_REQUEST_URN.getUrn(), result.getString(TITLE));
                                assertEquals("Policy could not be deleted, count not equal to list of policies to be deleted", result.getString(DETAIL));
                                vertxTestContext.completeNow();
                            }
                        });
    }

    @Test
    @DisplayName("Test initiateDeletePolicy with invalid User")
    public void testInitiateDeletePolicyWithInvalidUser(VertxTestContext vertxTestContext)
    {
        policyList = new JsonArray();
        policyList.add(new JsonObject().put("id", utility.getPolicyId()));

        JsonObject jsonObject = new JsonObject()
                .put("userId", utility.getConsumerId())
                .put("userRole", "consumer")
                .put("emailId", utility.getConsumerEmailId())
                .put("firstName", utility.getConsumerFirstName())
                .put("lastName", utility.getConsumerLastName());
        User consumer = new User(jsonObject);

        deletePolicy.initiateDeletePolicy(policyList, consumer).onComplete(handler -> {
            if (handler.succeeded()) {
                vertxTestContext.failNow("Succeeded with an invalid user");
            } else {
                JsonObject result =  new JsonObject(handler.cause().getMessage());
                assertEquals(400, result.getInteger(TYPE));
                assertEquals(ResponseUrn.BAD_REQUEST_URN.getUrn(), result.getString(TITLE));
                assertEquals("Policy could not be deleted, count not equal to list of policies to be deleted", result.getString(DETAIL));
                vertxTestContext.completeNow();
            }
        });
    }

    @Test
    @DisplayName("Test initiateDeletePolicy with duplicate policy id list")
    public void testInitiateDeletePolicy4DuplicatePolicy(VertxTestContext vertxTestContext)
    {
        policyList = new JsonArray();
        policyList.add(new JsonObject().put("id", utility.getPolicyId()));
        policyList.add(new JsonObject().put("id", utility.getPolicyId()));


        deletePolicy
                .initiateDeletePolicy(policyList, owner)
                .onComplete(
                        handler -> {
                            if (handler.succeeded()) {
                                vertxTestContext.failNow("Succeeded with an invalid policy in policy list");
                            } else {
                                JsonObject result =  new JsonObject(handler.cause().getMessage());

                                assertEquals(400, result.getInteger(TYPE));
                                assertEquals(ResponseUrn.BAD_REQUEST_URN.getUrn(), result.getString(TITLE));
                                assertEquals("Duplicate policy Ids present in the request", result.getString(DETAIL));
                                vertxTestContext.completeNow();
                            }
                        });
    }


    @Test
    @DisplayName("Test initiateDeletePolicy with null User")
    public void testInitiateDeletePolicyWithNullUser(VertxTestContext vertxTestContext)
    {

        assertThrows(NullPointerException.class, () -> deletePolicy.initiateDeletePolicy(policyList, null));
        vertxTestContext.completeNow();
    }

    @Test
    @DisplayName("Test initiateDeletePolicy with null policy list")
    public void testInitiateDeletePolicyWithNullPolicyList(VertxTestContext vertxTestContext)
    {
        assertThrows(NullPointerException.class, () -> deletePolicy.initiateDeletePolicy(null, owner));
        vertxTestContext.completeNow();
    }

    @Test
    @DisplayName("Test executeQuery method with invalid tuple")
    public void testExecuteQueryWithInvalidTuple(VertxTestContext vertxTestContext){
        deletePolicy.executeQuery(DELETE_POLICY_QUERY, Tuple.tuple(),handler -> {
            if(handler.succeeded())
            {
                vertxTestContext.failNow("Succeeded for invalid tuple");
            }
            else
            {
                JsonObject result = new JsonObject(handler.cause().getMessage());
                assertEquals(500, result.getInteger(TYPE));
                assertEquals(ResponseUrn.DB_ERROR_URN.getMessage(), result.getString(TITLE));
                assertEquals("Failure while executing query",result.getString(DETAIL));
                vertxTestContext.completeNow();
            }
        });
    }

    @Test
    @DisplayName("Test initiateDeletePolicy method when policy is already DELETED")
    public void testInitiateDeletePolicyWithDeletedPolicy(VertxTestContext vertxTestContext){
        UUID policy = UUID.randomUUID();
        policyList = new JsonArray();
        policyList.add(new JsonObject().put("id",policy));

        JsonObject jsonObject = new JsonObject()
                .put("userId", utility.getOwnerId())
                .put("userRole", "provider")
                .put("emailId", utility.getOwnerEmailId())
                .put("firstName", utility.getOwnerFirstName())
                .put("lastName", utility.getOwnerLastName());
        Tuple tuple = Tuple.of(policy, utility.getConsumerEmailId(), utility.getResourceId(), "RESOURCE_GROUP", utility.getOwnerId(), "DELETED", LocalDateTime.of(2030,1,1,1,1,1,1), "{}", LocalDateTime.of(2023,1,1,1,1,1,1), LocalDateTime.of(2024,1,1,1,1,1,1));
        utility.executeQuery(tuple,INSERT_INTO_POLICY_TABLE);
        deletePolicy.initiateDeletePolicy(policyList, new User(jsonObject)).onComplete(handler -> {
            if(handler.succeeded())
            {
                vertxTestContext.failNow("Succeeded for previously deleted policy");
            }
            else
            {
                JsonObject result =  new JsonObject(handler.cause().getMessage());
                assertEquals(400, result.getInteger(TYPE));
                assertEquals(ResponseUrn.BAD_REQUEST_URN.getUrn(), result.getString(TITLE));
                assertEquals("Policy could not be deleted, count not equal to list of policies to be deleted", result.getString(DETAIL));
                vertxTestContext.completeNow();
            }
        });
    }

    @Test
    @DisplayName("Test initiateDeletePolicy method when policy is already expired")
    public void testInitiateDeletePolicyWithExpiredPolicy(VertxTestContext vertxTestContext){
        UUID policy = UUID.randomUUID();
        policyList = new JsonArray();
        policyList.add(new JsonObject().put("id",policy));

        JsonObject jsonObject = new JsonObject()
                .put("userId", utility.getOwnerId())
                .put("userRole", "provider")
                .put("emailId", utility.getOwnerEmailId())
                .put("firstName", utility.getOwnerFirstName())
                .put("lastName", utility.getOwnerLastName());
        Tuple tuple = Tuple.of(policy, utility.getConsumerEmailId(), utility.getResourceId(), "RESOURCE_GROUP", utility.getOwnerId(), "DELETED", LocalDateTime.now(), "{}", LocalDateTime.of(2022,1,1,1,1,1,1), LocalDateTime.of(2022,2,1,1,1,1));
        utility.executeQuery(tuple,INSERT_INTO_POLICY_TABLE);
        deletePolicy.initiateDeletePolicy(policyList, new User(jsonObject)).onComplete(handler -> {
            if(handler.succeeded())
            {
                vertxTestContext.failNow("Succeeded for previously deleted policy");
            }
            else
            {
                JsonObject result =  new JsonObject(handler.cause().getMessage());
                assertEquals(400, result.getInteger(TYPE));
                assertEquals(ResponseUrn.BAD_REQUEST_URN.getUrn(), result.getString(TITLE));
                assertEquals("Policy could not be deleted, count not equal to list of policies to be deleted", result.getString(DETAIL));
                vertxTestContext.completeNow();
            }
        });
    }
    @Test
    @DisplayName("Test executeQuery with null tuple values")
    public void testExecuteQueryWithNullTuple(VertxTestContext vertxTestContext){
        assertThrows(NullPointerException.class, () -> deletePolicy.executeQuery(DELETE_POLICY_QUERY, Tuple.of(null,null),mock(Handler.class)));
        vertxTestContext.completeNow();
    }

    @Test
    @DisplayName("Test executeQuery with invalid query")
    public void testExecuteQueryWithInvalidQuery(VertxTestContext vertxTestContext)
    {
        String query = "UPDATE abcd SET status='DELETED' WHERE _id = ANY ('{shjdfgsfhguergugr}'::uuid[])";
        deletePolicy.executeQuery(
                query,
                Tuple.tuple(),
                handler -> {
                    if (handler.succeeded()) {
                        vertxTestContext.failNow("Succeeded for non-existent relation or table");
                    } else {
                        JsonObject result = new JsonObject(handler.cause().getMessage());
                        assertEquals(500, result.getInteger(TYPE));
                        assertEquals(ResponseUrn.DB_ERROR_URN.getMessage(), result.getString(TITLE));
                        assertEquals("Failure while executing query",result.getString(DETAIL));
                        vertxTestContext.completeNow();
                    }
                });

    }

}