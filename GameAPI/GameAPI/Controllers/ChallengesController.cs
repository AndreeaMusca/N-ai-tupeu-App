using GameAPI.Models;
using GameAPI.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace GameAPI.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class ChallengesController : ControllerBase
    {
        private readonly IChallengeCollectionService _challengesCollectionService;

        public ChallengesController(IChallengeCollectionService challengeCollectionService)
        {
            _challengesCollectionService = challengeCollectionService ?? throw new ArgumentNullException(nameof(challengeCollectionService));
        }

        /// <summary>
        /// Get all challenges
        /// </summary>
        /// <returns></returns>
        [HttpGet]
        public async Task<IActionResult> GetChallenges()
        {
            return Ok(await _challengesCollectionService.GetAll());
        }


        ///<summary>
        ///Get an challenge by user id
        ///</summary>
        ///<param name="userId">Introduce the ID</param>
        ///<returns></returns>
        [HttpGet("getChallengeByUserId/{userId}")]
        public async Task<IActionResult> GetChallengeByUserId(Guid userId)
        {
            var challenges = await _challengesCollectionService.GetAllChallengesByUserId(userId);

            if (challenges == null)
            {
                return NotFound();
            }

            return Ok(challenges);
        }


        [HttpPost]
        public async Task<IActionResult> AddChallenge([FromBody] ChallengeDto challengeDto)
        {
            if (string.IsNullOrEmpty(challengeDto.Text ))
            {
                return BadRequest("The text cannot be null");
            }

            Challenge challenge = new (challengeDto.Text,challengeDto.Type,challengeDto.UserId);
            var isCreated = await _challengesCollectionService.Create(challenge);
            if (!isCreated)
            {
                return BadRequest("Something went wrong");
            }

            return CreatedAtAction(nameof(GetChallengeById), new { id = challenge.Id }, challenge);
        }


        ///<summary>
        ///Get a challenge by id
        ///</summary>
        ///<param name="id">Introduce the ID</param>
        ///<returns></returns>
        [HttpGet("getChallengeById/{id}")]
        public async Task<IActionResult> GetChallengeById(Guid id)
        {
            var challenge = await _challengesCollectionService.Get(id);

            if (challenge == null)
            {
                return NotFound();
            }

            return Ok(challenge);
        }



    }

}
