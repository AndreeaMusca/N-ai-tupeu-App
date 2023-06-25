using GameAPI.Models;
using GameAPI.Settings;
using MongoDB.Driver;

namespace GameAPI.Services
{
    public class AccountCollectionService : IAccountCollectionService
    {
        private readonly IMongoCollection<Account> _accounts;


        public AccountCollectionService(IMongoDBSettings settings)
        {
            var client = new MongoClient(settings.ConnectionString);
            var database = client.GetDatabase(settings.DatabaseName);

            _accounts = database.GetCollection<Account>(settings.AccountsCollectionName);
        }

        public async Task<bool> Create(Account model)
        {
            await _accounts.InsertOneAsync(model);

            return true;
        }

        public Task<bool> Delete(Guid id)
        {
            throw new NotImplementedException();
        }

        public async Task<Account> Get(Guid id)
        {
            return (await _accounts.FindAsync(account => account.Id == id)).FirstOrDefault();
        }

        public async Task<Account> GetAccountByUsernameAndPassword(string username, string password)
        {
            return await _accounts.Find(account => account.Username == username && account.Password == password).FirstOrDefaultAsync();
        }


        public async Task<List<Account>> GetAll()
        {
            var result = await _accounts.FindAsync(account => true);

            return result.ToList();
        }

        public Task<bool> Update(Guid id, Account model)
        {
            throw new NotImplementedException();
        }

        
    }
}
