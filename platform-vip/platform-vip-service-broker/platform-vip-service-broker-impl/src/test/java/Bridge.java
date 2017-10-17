package com.siwasoftware.platform.samplebridge;

/**
 *
 */
public class Bridge {

    /*

    private static final String WATERMARK = "testbridge";

    private static final H2DataSourceFactory.H2Config h2Config = new H2DataSourceFactory.H2Config() {{
        //url = "jdbc:jdbcdslog:h2adapter:file:./lib-msa-h2adapter/db/bridgedb;targetDriver=org.hsqldb.jdbcDriver";
        url = "jdbc:h2:file:./platform-vip-adapter-db-h2/db/bridgedb;IFEXISTS=TRUE;LOCK_MODE=1"; // lock_mode=1 = serialised mode - VERY important for back-end.
        username = "admin";
        password = "password";
        //username = "";
        //password = "";
        maxConnections = 10;
        trackStatistics = false;
        debugMode = true;
    }};

    private static final SchemaConfig schemaConfig = new SchemaConfig() {{
        buildSchema = false;
        migrationsDir = "db.migration";
    }};

    private static final TaskServiceImpl.ServiceConfig taskServiceConfig = new TaskServiceImpl.ServiceConfig() {{
        lockTtl = 0;
    }};

    private static final DaoManager.SchemaConfig dataStoreSchemaConfig = new DaoManager.SchemaConfig() {{
        catalog = "BRIDGEDB";
        packages = ImmutableMap.of(
                "com.siwasoftware.platform", "PUBLIC"
        );
        profiles = new ImmutableSet.Builder<ColumnTypeMappingProfile>()
                .add(H2Profile.additionalH2Support(true))
                .add(ExtensionProfile.additionalAutoboxingSupport())
                .add(ExtensionProfile.additionalDateSupport())
                .add(ExtensionProfile.additionalLobSupport())
                .build();
        pojos = new ImmutableSet.Builder<Class<?>>()
                .add(Event.class)
                .add(Task.class)
                .add(Watch.class)
                .build();
    }};

    private static final DaoManager.Config dataStoreConfig = new DaoManager.Config() {{
        dynamicRegistration = true;
    }};

    private final IntegrationServer server;
    private final TaskService taskService;
    private final AuditService auditService;
    private final TaskServiceClient taskClient;
    private final AuditServiceClient auditClient;

    public Bridge() {

        final Configs configs = new Configs()
                .register(h2Config)
                .register(schemaConfig)
                .register(dataStoreConfig)
                .register(dataStoreSchemaConfig)
                .register(taskServiceConfig);

        final DaoManager manager = new H2DaoManagerFactory()
                .newDaoManager(configs);

        final Indexer indexer = new IndexerFactory()
                .configure(new CoreIndexerConfiguration())
                .configure(new IntegrationIndexerConfiguration())
                .build();

        final Auditor auditor = new Auditor(indexer);

        final Services services = new Services()
                .register(manager)
                .register(indexer)
                .register(auditor);

        this.taskService = new TaskServiceImpl(
                configs,
                services);

        this.auditService = new AuditServiceImpl(
                configs,
                services);

        this.server = new IntegrationServer(
                this.taskService,
                this.auditService);

        this.taskClient = new TaskServiceClient("127.0.0.1", 8081);
        this.auditClient = new AuditServiceClient("127.0.0.1", 8081);

    }

    public static void main(String[] args) {

        final Bridge bridge = new Bridge();

        try {

            //runLocalTest(bridge);
            runWebTest(bridge);

        } finally {

            //bridge.queue.clearTaskQueue(WATERMARK, "queue1");

        }

    }

    private static void runWebTest(final Bridge bridge) {

        // queue functions

        final Integer lockId = bridge.taskClient.lockRequestedQueue(null,"testbridge", "queue1");
        bridge.taskClient.clearTaskQueue(lockId);
        System.out.println("Obtained lock: " + lockId);

        System.out.println("Queue cleared");

        final Integer id = bridge.taskClient.submitTask(lockId, new TaskSubmission(new TaskSubmission.Init() {{
            state = "state1";
            step = "begin";
            data = "{data:'somedata'}";
            triggerDate = new Date();
        }}));
        System.out.println("Created task: " + id);

        final TaskUpdate update = new TaskUpdate(
                id,
                "state2",
                "end",
                "{data:'somenewdata'}");

        bridge.taskClient.updateTask(lockId, update);
        System.out.println("Updated");

        bridge.taskClient.releaseQueueLock(lockId);
        System.out.println("Released lock");

        // audit functions
        final List<AuditRecord> audits = bridge.auditClient.listAuditRecords(
                "testbridge.queue1",
                new Date(2000,0,1),
                new Date(2020,11,31));
        System.out.println("Tasks: " + audits);

        final List<AuditRecord> trail = bridge.auditClient.listAuditTrail(audits.get(0).getTargetVersionId());
        System.out.println("Trail: " + trail);
    }

    private static void runLocalTest(final Bridge bridge) {

        addTask(bridge, "queue1");
        addTask(bridge, "queue1");
        addTask(bridge, "queue1");

        updateTask(bridge, "queue1", "state1", "step1", "data");
        updateTask(bridge, "queue1", "state1", "step2", "data");
        updateTask(bridge, "queue1", "state2", "step1", "data");

        System.out.println("AUDIT: " + bridge.auditService.listAuditRecords(
                WATERMARK + ".queue1",
                new Date(2000, 0, 1),
                new Date(2020, 11, 31)));

    }

    private static void addTask(Bridge bridge, String q) {

        Integer lockId = bridge.taskClient.lockQueue(null,WATERMARK);

        bridge.taskClient.submitTask(lockId, new TaskSubmission(new TaskSubmission.Init() {{
            this.state = "state1";
            this.step = "step1";
            this.triggerDate = new Date();
            this.data = "{somedata='none'}";
        }}));

        bridge.taskClient.releaseQueueLock(lockId);
    }

    private static void updateTask(Bridge bridge, String queue, String state, String step, String data) {

        Integer lockId = bridge.taskClient.lockQueue(null,WATERMARK);

        final TaskInfo info = bridge.taskClient.nextTask(lockId);
        if (info != null) bridge.taskClient.updateTask(lockId, info.toUpdate().update(state, step, data));

        bridge.taskClient.releaseQueueLock(lockId);

    }

*/

}
