namespace GameAPI.Models
{
    public class Account
    {
        public Account(string? username, string? password)
        {
            Id= Guid.NewGuid();
            Username = username;
            Password = password;
        }

        public Guid Id { get; set; }
        public string? Username { get; set; }
        public string? Password { get; set; }
    }
}
