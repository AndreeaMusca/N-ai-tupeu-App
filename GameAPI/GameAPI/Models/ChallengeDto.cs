namespace GameAPI.Models
{
    public class ChallengeDto
    {
        public string? Text { get; set; }
        public ChallengeType.Type Type { get; set; }
        public Guid UserId { get; set; }

    }
}
