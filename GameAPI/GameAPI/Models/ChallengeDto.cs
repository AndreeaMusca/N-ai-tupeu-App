using System.Text.Json.Serialization;

namespace GameAPI.Models
{
    public class ChallengeDto
    {
        public string? Text { get; set; }

        [JsonConverter(typeof(JsonStringEnumConverter))]
        public ChallengeType.Type Type { get; set; }
        public Guid UserId { get; set; }
        public ChallengeDto(Challenge challenge) { 
            Text = challenge.Text;
            Type = challenge.Type;
            UserId = challenge.UserId;
        }
        public ChallengeDto() { }
        public ChallengeDto(string text, ChallengeType.Type type, Guid userId)
        {
            Text = text;
            Type = type;
            UserId = userId;
        }



        public override bool Equals(object? obj)
        {
            return obj is ChallengeDto dto &&
                   Text == dto.Text &&
                   Type == dto.Type &&
                   UserId.Equals(dto.UserId);
        }
    }
}
