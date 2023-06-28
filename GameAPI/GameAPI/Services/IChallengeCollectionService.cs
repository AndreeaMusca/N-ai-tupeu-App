using GameAPI.Models;

namespace GameAPI.Services
{
    public interface IChallengeCollectionService:ICollectionService<Challenge>
    {
        Task<List<Challenge>> GetAllChallengesByUserId(Guid userId);
        Task<Challenge> GetByAttributes(string text, ChallengeType.Type type, Guid userId);

    }
}
