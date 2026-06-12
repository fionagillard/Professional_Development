/* Date:29/05/2026
Title: C++/Arduino-based water-level monitoring system for plant reservoir
Module: XE703, Professional Development
Deisgner: Fiona Gillard

Description:
This system monitors the water level within a plant reservoir using a water level sensor. 
The Arduino converts the sensor reading into a percentage and indicates the
system status using LEDs and a buzzer.

Status Levels:
- NORMAL
- LOW
- CRITICAL
*/

// PIN ASSIGNMENTS
// ======================================================

// Water level sensor connected to analogue input A0
const int waterSensorPin = A0;

// LED indicators
const int greenLedPin = 8;    // Normal condition
const int yellowLedPin = 9;   // Warning condition
const int redLedPin = 10;     // Critical condition

// Buzzer output
const int buzzerPin = 11;


// SENSOR CALIBRATION VALUES
// ======================================================

// Reading when sensor is dry
const int dryValue = 0;

// Reading when sensor is fully submerged
const int fullValue = 650;


// WATER LEVEL THRESHOLDS
// ======================================================

// 30% triggers warning
const int lowLevel = 30; 

// 15% triggers a critical alarm
const int criticalLevel = 15;

void setup()
{
    // Start serial communication
    Serial.begin(9600);

    // Configure LEDs as outputs
    pinMode(greenLedPin, OUTPUT);
    pinMode(yellowLedPin, OUTPUT);
    pinMode(redLedPin, OUTPUT);

    // Configure buzzer as output
    pinMode(buzzerPin, OUTPUT);

    // Print startup message
    Serial.println("=================================");
    Serial.println("Water Level Monitoring System");
    Serial.println("=================================");
    Serial.println("Raw Value, Water %, Status");
}

// MAIN LOOP
// ======================================================

void loop()
{
    // Read analogue value from sensor
    int rawValue = analogRead(waterSensorPin);

    // Convert sensor value to percentage
    int waterPercent =
        map(rawValue, dryValue, fullValue, 0, 100);

    // Prevent values outside 0-100%
    waterPercent =
        constrain(waterPercent, 0, 100);

    // Print sensor information
    Serial.print(rawValue);
    Serial.print(", ");

    Serial.print(waterPercent);
    Serial.print("%, ");

    // CRITICAL WATER LEVEL
    // ==================================================

    if (waterPercent <= criticalLevel)
    {
        // Turn on red LED
        digitalWrite(redLedPin, HIGH);

        // Turn off other LEDs
        digitalWrite(yellowLedPin, LOW);
        digitalWrite(greenLedPin, LOW);

        // Activate buzzer
        digitalWrite(buzzerPin, HIGH);

        // Display system status
        Serial.println("CRITICAL - Reservoir Almost Empty");
    }

    // LOW WATER LEVEL
    // ==================================================

    else if (waterPercent <= lowLevel)
    {
        // Turn on warning LED
        digitalWrite(yellowLedPin, HIGH);

        // Turn off other LEDs
        digitalWrite(redLedPin, LOW);
        digitalWrite(greenLedPin, LOW);

        // Buzzer remains off
        digitalWrite(buzzerPin, LOW);

        // Display system status
        Serial.println("LOW - Refill Required");
    }

    // NORMAL WATER LEVEL
    // ==================================================

    else
    {
        // Turn on green LED
        digitalWrite(greenLedPin, HIGH);

        // Turn off warning devices
        digitalWrite(yellowLedPin, LOW);
        digitalWrite(redLedPin, LOW);
        digitalWrite(buzzerPin, LOW);

        // Display system status
        Serial.println("NORMAL - Water Level OK");
    }


    // Waits one second before next reading
    delay(1000);
}
