using GameAPI.Models;

namespace GameAPI.Services
{
    public interface IAccountCollectionService:ICollectionService<Account>
    {
        Task<Account> GetAccountByUsernameAndPassword(string username, string password);
        Task<bool>  Create(string username, string password);
    }
}
