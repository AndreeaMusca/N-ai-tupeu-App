using GameAPI.Models;

namespace GameAPI.Services
{
    public interface IChallengeCollectionService:ICollectionService<Challenge>
    {
        Task<List<Challenge>> GetAllChallengesByUserId(Guid userId);
    }
}
