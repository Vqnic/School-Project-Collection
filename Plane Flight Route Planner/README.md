# Plane Flight Route Planner
This assignment was for my **CSCI A360 Software Engineering** class.

## Project Requirements:
- **Airplane Database**:
  - Airplanes must be stored to file with airplane-specific data:
    - Make
    - Model
    - Airplane Type:
      - Prop
      - Jet
      - Turboprop
    - Size of fuel tank *(in litres)*
    - Fuel burn at cruise power *(in litres per hour)*
    - Airspeed at cruise power *(in knots)*
  - The user must be able to add airplanes to the database.
  - The user must be able to delete existing airplanes in the database.
- **Airport Database**:
  - Airports must be stored to file with airport-specific data:
    - ICAO identifier
    - Name
    - Latitude
    - Longitude
    - Available Fuel Types:
      - AVGAS *(for prop planes)*
      - JA-a *(for jets and turboprop planes)*
  - The user must be able to add airports to the database.
  - The user must be able to delete existing airports in the database.
- **Flight Planning**
  -  The user must be able to select a starting airport, an airplane from that airport, and a destination airport:
  -  The flight planning system must consider the selected airplane's refueling requirements:
    -  If a flight is impossible without refueling, it needs to find a nearby airport with an appropriate fuel type for the selected plane.
      -  If the flight is still impossible and no refueling options are available, it must display that.
  -  The flight planning system will include the following information for all flights:
    -  Starting airport name
    -  Heading
    -  Time to destination based on airspeed of selected plane
    -  Distance traveled
    -  Destination airport name
- Displays “THIS SOFTWARE IS NOT TO BE USED FOR FLIGHT PLANNING OR NAVIGATIONAL PURPOSE” on startup.