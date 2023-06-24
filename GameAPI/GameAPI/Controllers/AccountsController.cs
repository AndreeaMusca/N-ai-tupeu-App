using Microsoft.AspNetCore.Mvc;
using GameAPI.Models;
using GameAPI.Services;
using System;
using System.ComponentModel.DataAnnotations;
using System.Threading.Tasks;

namespace GameAPI.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class AccountsController : ControllerBase
    {
        private readonly IAccountCollectionService _accountCollectionService;

        public AccountsController(IAccountCollectionService accountCollectionService)
        {
            _accountCollectionService = accountCollectionService ?? throw new ArgumentNullException(nameof(accountCollectionService));
        }

        ///<summary>
        ///Get an account by id
        ///</summary>
        ///<param name="id">Introduce the ID</param>
        ///<returns></returns>
        [HttpGet("getById/{id}")]
        public async Task<IActionResult> GetAccountById(Guid id)
        {
            var announcement = await _accountCollectionService.Get(id);

            if (announcement == null)
            {
                return NotFound();
            }

            return Ok(announcement);
        }

        /// <summary>
        /// Get all accounts
        /// </summary>
        /// <returns></returns>
        [HttpGet]
        public async Task<IActionResult> GetAccounts()
        {
            return Ok(await _accountCollectionService.GetAll());
        }


        [HttpPost]
        public async Task<IActionResult> CreateAccount([FromBody] AccountDto accountDto)
        {
            if (string.IsNullOrEmpty(accountDto.Username) || string.IsNullOrEmpty(accountDto.Password))
            {
                return BadRequest("Username and password cannot be null or empty");
            }

            Account account = new Account(accountDto.Username, accountDto.Password);
            var isCreated = await _accountCollectionService.Create(account);
            if (!isCreated)
            {
                return BadRequest("Something went wrong");
            }

            return CreatedAtAction(nameof(GetAccountById), new { id = account.Id }, account);
        }



        [HttpPost("getByUsernameAndPassword")]
        public async Task<IActionResult> GetAccountByUsernameAndPassword([FromBody] AccountDto account)
        {
            if (string.IsNullOrEmpty(account.Username) || string.IsNullOrEmpty(account.Password))
            {
                return BadRequest("Username and password cannot be null or empty");
            }

            var result = await _accountCollectionService.GetAccountByUsernameAndPassword(account.Username, account.Password);

            if (result == null)
            {
                return NotFound("No account found with the provided credentials");
            }

            var response = new
            {
                result.Id
            };

            return Ok(response);
        }



    }
}
