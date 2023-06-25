namespace GameAPI.Models
{
    public class Challenge
    {
        public Challenge(string? text, ChallengeType.Type type, Guid userId)
        {
            Id = Guid.NewGuid();
            Text = text;
            Type = type;
            UserId = userId;
        }

        public Guid Id { get; set; }
        public string? Text { get; set; }
        public ChallengeType.Type Type {get;set;}
        public Guid UserId { get; set; }
    }
}
