using GameAPI.Models;
using GameAPI.Settings;
using MongoDB.Driver;
using MongoDB.Driver.Core.Authentication;

namespace GameAPI.Services
{
    public class ChallengeCollectionService : IChallengeCollectionService
    {
        private readonly IMongoCollection<Challenge> _challenges;

        public ChallengeCollectionService(IMongoDBSettings settings)
        {
            var client = new MongoClient(settings.ConnectionString);
            var database = client.GetDatabase(settings.DatabaseName);
            _challenges = database.GetCollection<Challenge>(settings.ChallengeCollectionName);
        }

        public async Task<bool> Create(Challenge model)
        {
            model.Id = Guid.NewGuid();
            await _challenges.InsertOneAsync(model);
            return true;
            
        }

        public async Task<bool> Delete(Guid id)
        {
            var challenge=await _challenges.FindAsync(c=>c.Id==id);
            if (challenge==null)
            {
                return false;
            }
            var isDeleted=await _challenges.DeleteOneAsync(c=>c.Id==id);
            return isDeleted.IsAcknowledged && isDeleted.DeletedCount > 0;
        }

        public async Task<Challenge> Get(Guid id)
        {
            return (await _challenges.FindAsync(c=>c.Id==id)).FirstOrDefault();
        }

        public async Task<List<Challenge>> GetAll()
        {
            var result = await _challenges.FindAsync(challenge => true);
            return result.ToList();
        }

        public async Task<List<Challenge>> GetAllChallengesByUserId(Guid userId)
        {
            return (await _challenges.FindAsync(c => c.UserId == userId)).ToList(); 
        }

        public async Task<bool> Update(Guid id, Challenge model)
        {
            var existingChallenge = await _challenges.ReplaceOneAsync(c => c.Id == id, model);
            if(existingChallenge.IsAcknowledged==false&&existingChallenge.MatchedCount==0)
            {
                await _challenges.InsertOneAsync(model);
            }
            return(existingChallenge.IsAcknowledged&&existingChallenge.MatchedCount>0);
        }
    }
}
