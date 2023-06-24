namespace GameAPI.Settings
{
    public class MongoDBSettings : IMongoDBSettings
    {
        public string AccountsCollectionName { get; set; }
        public string ConnectionString { get ; set; }
        public string DatabaseName { get ; set ; }
    }
}
