/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package smarthome;

/**
 *
 * @author poornimaepy
 */
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// Observer interface
interface Observer {
    void update();
}

// Subject interface
interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}

// Concrete Subject
class SmartHomeHub implements Subject {
    private List<Observer> observers;
    private Map<Integer, SmartDevice> devices;
    private List<ScheduledTask> scheduledTasks;
    private List<Trigger> triggers;

    public SmartHomeHub() {
        observers = new ArrayList<>();
        devices = new HashMap<>();
        scheduledTasks = new ArrayList<>();
        triggers = new ArrayList<>();
    }

    public void registerObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public void addDevice(SmartDevice device) {
        devices.put(device.getId(), device);
        registerObserver(device);
    }

    public void turnOn(int id) {
        SmartDevice device = devices.get(id);
        if (device != null) {
            device.turnOn();
            notifyObservers();
        }
    }

    public void turnOff(int id) {
        SmartDevice device = devices.get(id);
        if (device != null) {
            device.turnOff();
            notifyObservers();
        }
    }

    public void setSchedule(int id, LocalTime time, String command) {
        scheduledTasks.add(new ScheduledTask(id, time, command));
    }

    public void addTrigger(String condition, String operator, int value, String action) {
        triggers.add(new Trigger(condition, operator, value, action));
    }

    public String statusReport() {
        StringBuilder report = new StringBuilder();
        for (SmartDevice device : devices.values()) {
            report.append(device.status()).append(". ");
        }
        return report.toString().trim();
    }

    public String scheduledTasksReport() {
        StringBuilder report = new StringBuilder();
        report.append("[");
        for (ScheduledTask task : scheduledTasks) {
            report.append("{device: ").append(task.id)
                  .append(", time: \"").append(task.time.format(DateTimeFormatter.ofPattern("HH:mm")))
                  .append("\", command: \"").append(task.command).append("\"}, ");
        }
        if (!scheduledTasks.isEmpty()) {
            report.setLength(report.length() - 2); // Remove last comma and space
        }
        report.append("]");
        return report.toString();
    }

    public String triggersReport() {
        StringBuilder report = new StringBuilder();
        report.append("[");
        for (Trigger trigger : triggers) {
            report.append("{condition: \"").append(trigger.condition)
                  .append(" ").append(trigger.operator).append(" ").append(trigger.value)
                  .append("\", action: \"").append(trigger.action).append("\"}, ");
        }
        if (!triggers.isEmpty()) {
            report.setLength(report.length() - 2); // Remove last comma and space
        }
        report.append("]");
        return report.toString();
    }

    public void executeScheduledTasks() {
        LocalTime now = LocalTime.now();
        for (ScheduledTask task : scheduledTasks) {
            if (task.time.equals(now)) {
                if (task.command.equalsIgnoreCase("turn on")) {
                    turnOn(task.id);
                } else if (task.command.equalsIgnoreCase("turn off")) {
                    turnOff(task.id);
                }
            }
        }
    }

    public void evaluateTriggers() {
        for (Trigger trigger : triggers) {
            for (SmartDevice device : devices.values()) {
                if (trigger.condition.equalsIgnoreCase("temperature") && device instanceof Thermostat) {
                    Thermostat thermostat = (Thermostat) device;
                    if (trigger.operator.equals(">") && thermostat.getTemperature() > trigger.value) {
                        if (trigger.action.equalsIgnoreCase("turnOff(1)")) {
                            turnOff(1);
                        }
                    }
                }
            }
        }
    }
}

// Abstract SmartDevice class
abstract class SmartDevice implements Observer {
    protected int id;
    protected String type;

    public SmartDevice(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    abstract void turnOn();
    abstract void turnOff();
    abstract String status();
}

// Concrete Light class
class Light extends SmartDevice {
    private boolean status;

    public Light(int id) {
        super(id, "light");
        this.status = false;
    }

    @Override
    void turnOn() {
        status = true;
    }

    @Override
    void turnOff() {
        status = false;
    }

    @Override
    String status() {
        return "Light " + id + " is " + (status ? "On" : "Off");
    }

    @Override
    public void update() {
        // Custom update logic if needed
    }
}

// Concrete Thermostat class
class Thermostat extends SmartDevice {
    private int temperature;

    public Thermostat(int id, int temperature) {
        super(id, "thermostat");
        this.temperature = temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getTemperature() {
        return temperature;
    }

    @Override
    void turnOn() {
        // Do nothing for thermostat
    }

    @Override
    void turnOff() {
        // Do nothing for thermostat
    }

    @Override
    String status() {
        return "Thermostat " + id + " is set to " + temperature + " degrees";
    }

    @Override
    public void update() {
        // Custom update logic if needed
    }
}

// Concrete DoorLock class
class DoorLock extends SmartDevice {
    private boolean status;

    public DoorLock(int id) {
        super(id, "door lock");
        this.status = true; // Assume door is locked by default
    }

    @Override
    void turnOn() {
        status = false; // Unlock the door
    }

    @Override
    void turnOff() {
        status = true; // Lock the door
    }

    @Override
    String status() {
        return "Door " + id + " is " + (status ? "Locked" : "Unlocked");
    }

    @Override
    public void update() {
        // Custom update logic if needed
    }
}

// Factory Method for creating smart devices
class SmartDeviceFactory {
    public static SmartDevice createDevice(String type, int id) {
        switch (type.toLowerCase()) {
            case "light":
                return new Light(id);
            case "thermostat":
                return new Thermostat(id, 70); // default temperature
            case "door lock":
                return new DoorLock(id);
            default:
                throw new IllegalArgumentException("Unknown device type");
        }
    }
}

// Proxy Pattern for controlling access to the devices
class SmartDeviceProxy extends SmartDevice {
    private SmartDevice device;

    public SmartDeviceProxy(SmartDevice device) {
        super(device.getId(), device.getType());
        this.device = device;
    }

    @Override
    void turnOn() {
        // Check permissions, then forward request
        device.turnOn();
    }

    @Override
    void turnOff() {
        // Check permissions, then forward request
        device.turnOff();
    }

    @Override
    String status() {
        return device.status();
    }

    @Override
    public void update() {
        device.update();
    }
}

// Helper classes for scheduling and triggers
class ScheduledTask {
    int id;
    LocalTime time;
    String command;

    public ScheduledTask(int id, LocalTime time, String command) {
        this.id = id;
        this.time = time;
        this.command = command;
    }
}

class Trigger {
    String condition;
    String operator;
    int value;
    String action;

    public Trigger(String condition, String operator, int value, String action) {
        this.condition = condition;
        this.operator = operator;
        this.value = value;
        this.action = action;
    }
}

// Main class to demonstrate the Smart Home System
public class Smarthome{
    public static void main(String[] args) {
        SmartHomeHub hub = new SmartHomeHub();

        // Sample Inputs
        String devicesInput = "[{id: 1, type: 'light', status: 'off'}, {id: 2, type: 'thermostat', temperature: 70}, {id: 3, type: 'door lock', status: 'locked'}]";
        String commandsInput = "['turnOn(1)', 'setSchedule(2, \"06:00\", \"Turn On\")', 'addTrigger(\"temperature\", \">\", 75, \"turnOff(1)\")']";

        // Initialize devices using Factory Method
        List<Map<String, String>> devicesList = parseDevices(devicesInput);
        for (Map<String, String> deviceData : devicesList) {
            int id = Integer.parseInt(deviceData.get("id"));
            String type = deviceData.get("type");
            SmartDevice device = SmartDeviceFactory.createDevice(type, id);
            SmartDevice proxyDevice = new SmartDeviceProxy(device);
            hub.addDevice(proxyDevice);

            // Set initial states
            if (type.equalsIgnoreCase("thermostat")) {
                ((Thermostat) device).setTemperature(Integer.parseInt(deviceData.get("temperature")));
            } else if (type.equalsIgnoreCase("light") || type.equalsIgnoreCase("door lock")) {
                if (deviceData.get("status").equalsIgnoreCase("on")) {
                    device.turnOn();
                } else {
                    device.turnOff();
                }
            }
        }

        // Execute commands
        List<String> commandsList = parseCommands(commandsInput);
        for (String command : commandsList) {
            executeCommand(hub, command);
        }

        // Output reports
        System.out.println("Status Report: \"" + hub.statusReport() + "\"");
        System.out.println("Scheduled Tasks: \"" + hub.scheduledTasksReport() + "\"");
        System.out.println("Automated Triggers: \"" + hub.triggersReport() + "\"");
    }

    private static List<Map<String, String>> parseDevices(String input) {
        List<Map<String, String>> devicesList = new ArrayList<>();
        input = input.substring(1, input.length() - 1); // Remove brackets
        String[] devicesArray = input.split("}, ");
        for (String deviceStr : devicesArray) {
            deviceStr = deviceStr.replace("{", "").replace("}", "");
            String[] keyValuePairs = deviceStr.split(", ");
            Map<String, String> deviceData = new HashMap<>();
            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split(": ");
                deviceData.put(keyValue[0], keyValue[1].replace("'", ""));
            }
            devicesList.add(deviceData);
        }
        return devicesList;
    }

    private static List<String> parseCommands(String input) {
        input = input.substring(1, input.length() - 1); // Remove brackets
        String[] commandsArray = input.split(", ");
        return Arrays.asList(commandsArray);
    }

    private static void executeCommand(SmartHomeHub hub, String command) {
        if (command.startsWith("turnOn(")) {
            int id = Integer.parseInt(command.substring(7, command.length() - 1));
            hub.turnOn(id);
        } else if (command.startsWith("turnOff(")) {
            int id = Integer.parseInt(command.substring(8, command.length() - 1));
            hub.turnOff(id);
        } else if (command.startsWith("setSchedule(")) {
            String[] parts = command.substring(12, command.length() - 1).split(", ");
            int id = Integer.parseInt(parts[0]);
            LocalTime time = LocalTime.parse(parts[1].replace("\"", ""), DateTimeFormatter.ofPattern("HH:mm"));
            String cmd = parts[2].replace("\"", "");
            hub.setSchedule(id, time, cmd);
        } else if (command.startsWith("addTrigger(")) {
            String[] parts = command.substring(11, command.length() - 1).split(", ");
            String condition = parts[0].replace("\"", "");
            String operator = parts[1].replace("\"", "");
            int value = Integer.parseInt(parts[2]);
            String action = parts[3].replace("\"", "");
            hub.addTrigger(condition, operator, value, action);
        }
    }
}
