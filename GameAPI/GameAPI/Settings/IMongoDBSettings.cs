namespace GameAPI.Settings
{
    public interface IMongoDBSettings
    {
        string AccountsCollectionName { get; set; }
        string ConnectionString { get; set; }
        string DatabaseName { get; set; }
    }

}
