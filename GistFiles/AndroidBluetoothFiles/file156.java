private String decodeURL(int Value)
    {
        switch(Value)
        {
            case 0:
                return ".com/";

            case 1:
                return ".org/";

            case 2:
                return ".edu/";

            case 3:
                return ".net/";

            case 4:
                return ".info/";

            case 5:
                return ".biz/";

            case 6:
                return ".gov/";

            case 7:
                return ".com";

            case 8:
                return ".org";

            case 9:
                return ".edu";

            case 10:
                return ".net";

            case 11:
                return ".info";

            case 12:
                return ".biz";

            case 13:
                return ".gov";

        }

        if((Value > 0x20) && (Value < 0x7f))
        {
            return String.format("%c",Value);
        }

        return "";
    }
