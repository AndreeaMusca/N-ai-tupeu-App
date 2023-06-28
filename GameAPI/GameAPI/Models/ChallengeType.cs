using Newtonsoft.Json.Converters;
using System.Text.Json.Serialization;

namespace GameAPI.Models
{

    public class ChallengeType
    {
        //[JsonConverter(typeof(StringEnumConverter))]
        public enum Type
        {
            Truth,
            Dare
        }

    }
}
