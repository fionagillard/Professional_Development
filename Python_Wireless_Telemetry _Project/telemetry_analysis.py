# Date: 22/04/2026
# Module: XE703, Professional Development
# Designer: Fiona Gillard

# Description: Python-based UAV telemetry monitoring, fault detection and 
# communication performance analysis.

import pandas as pd
import matplotlib.pyplot as plt

# Load the telemetry CSV file
data = pd.read_csv("telemetry_data.csv")

# Show the column names to check the CSV has loaded correctly
print("CSV columns:")
print(data.columns)

# Show the full dataset
print("\nTelemetry data:")
print(data)

print("\nFAULT DETECTION RESULTS:")

for index, row in data.iterrows():

    if row["Battery_Percent"] <= 50:
        print("LOW BATTERY WARNING at", row["Time_s"], "seconds")

    if row["RSSI_dBm"] <= -85:
        print("WEAK SIGNAL WARNING at", row["Time_s"], "seconds")

    if row["Latency_ms"] > 200:
        print("HIGH LATENCY WARNING at", row["Time_s"], "seconds")

    if row["Packet_Lost"] == "Yes":
        print("PACKET LOSS WARNING at", row["Time_s"], "seconds")


average_latency = data["Latency_ms"].mean()
maximum_latency = data["Latency_ms"].max()
minimum_latency = data["Latency_ms"].min()

total_packets = len(data)
lost_packets = data[data["Packet_Lost"] == "Yes"].shape[0]
packet_loss_percentage = (lost_packets / total_packets) * 100

print("\nCOMMUNICATION PERFORMANCE:")
print("Average latency:", round(average_latency, 2), "ms")
print("Maximum latency:", maximum_latency, "ms")
print("Minimum latency:", minimum_latency, "ms")
print("Total packets:", total_packets)
print("Lost packets:", lost_packets)
print("Packet loss:", round(packet_loss_percentage, 2), "%")

if average_latency < 200:
    print("PASS: Average transmission delay is below 200 ms")
else:
    print("FAIL: Average transmission delay exceeds 200 ms")


# Graph 1: Battery percentage
plt.figure()
plt.plot(data["Time_s"], data["Battery_Percent"], marker="o")
plt.xlabel("Time (seconds)")
plt.ylabel("Battery (%)")
plt.title("Drone Battery Percentage Over Time")
plt.grid(True)
plt.show()

# Graph 2: Battery voltage
plt.figure()
plt.plot(data["Time_s"], data["Battery_V"], marker="o")
plt.xlabel("Time (seconds)")
plt.ylabel("Battery Voltage (V)")
plt.title("Drone Battery Voltage Over Time")
plt.grid(True)
plt.show()

# Graph 3: Signal strength
plt.figure()
plt.plot(data["Time_s"], data["RSSI_dBm"], marker="o")
plt.xlabel("Time (seconds)")
plt.ylabel("RSSI (dBm)")
plt.title("Wireless Signal Strength Over Time")
plt.grid(True)
plt.show()

# Graph 4: Altitude
plt.figure()
plt.plot(data["Time_s"], data["Altitude_m"], marker="o")
plt.xlabel("Time (seconds)")
plt.ylabel("Altitude (m)")
plt.title("Drone Altitude During Mission")
plt.grid(True)
plt.show()