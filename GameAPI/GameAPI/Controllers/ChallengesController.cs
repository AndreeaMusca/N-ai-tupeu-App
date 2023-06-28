using GameAPI.Models;
using GameAPI.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

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
        ///Get a challenge by user id
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

        /// <summary>
        /// Add a challenge
        /// </summary>
        /// <returns></returns>

        [HttpPost]
        public async Task<IActionResult> AddChallenge([FromBody] ChallengeDto challengeDto)
        {
            if (string.IsNullOrEmpty(challengeDto.Text))
            {
                return BadRequest("The text cannot be null");
            }

            Challenge challenge = new (challengeDto.Text, challengeDto.Type, challengeDto.UserId);
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


        /// <summary>
        /// Sync Mongo and room
        /// </summary>
        /// <returns></returns>
        [HttpPost("migrateToMongo/{id}")]
        public async Task<IActionResult> MigrateChallengesToMongo([FromBody] List<ChallengeDto> challengesFromRoom,Guid id)
        {
            if (challengesFromRoom == null || challengesFromRoom.Count == 0)
            {
                return NoContent(); // No challenges found in the request body
            }

            // Check if there are any deleted challenges
            var challengesFromMongo = await _challengesCollectionService.GetAllChallengesByUserId(id);
            foreach (var challenge in challengesFromMongo)
            {
                if (!challengesFromRoom.Exists(c => c.Text == challenge.Text && c.Type == challenge.Type && c.UserId == challenge.UserId))
                {
                    await _challengesCollectionService.Delete(challenge.Id);
                }
            }

            var synchronizedChallenges = new List<ChallengeDto>();

            foreach (var challenge in challengesFromRoom)
            {
                // Check if the challenge already exists in MongoDB
                var existingChallenge = await _challengesCollectionService.GetByAttributes(challenge.Text, challenge.Type, challenge.UserId);
                if (existingChallenge == null)
                {
                    Challenge newChallenge = new(challenge.Text, challenge.Type, challenge.UserId);
                    await _challengesCollectionService.Create(newChallenge);
                }

                synchronizedChallenges.Add(challenge);
            }

            return Ok(synchronizedChallenges);
        }
    }
}
