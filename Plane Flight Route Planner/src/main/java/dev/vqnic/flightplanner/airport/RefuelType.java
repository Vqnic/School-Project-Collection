package dev.vqnic.flightplanner.airport;

public enum RefuelType {
  AVGAS,
  JA_A;

  @Override
  public String toString() {
    switch (this) {
      case AVGAS:
        return "AVGAS";
      case JA_A:
        return "JA-a";
      default:
        return "";
    }
  }

  public static RefuelType getEnum(String string){
    if(string.equalsIgnoreCase("AVGAS")) return RefuelType.AVGAS;
    return RefuelType.JA_A; //Literally only two, and JA-a is the only one with odd formatting.
  }
}
