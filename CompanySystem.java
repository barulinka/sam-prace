import java.util.*;
import java.util.logging.*;

class Employee {
    String firstName, lastName, position;
    int id;
    double salary;

    Employee(String firstName, String lastName, int id, String position, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.position = position;
        this.salary = salary;
    }

    public String toString() {
        return id + ": " + firstName + " " + lastName + " - " + position + ", Plat: " + salary;
    }
}

class EmployeeManager {
    private List<Employee> employees = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(EmployeeManager.class.getName());

    public void addEmployee(String firstName, String lastName, int id, String position, double salary) {
        employees.add(new Employee(firstName, lastName, id, position, salary));
        logger.info("Přidán zaměstnanec: " + id);
    }

    public void editEmployee(int id, String position, double salary) {
        for (Employee e : employees) {
            if (e.id == id) {
                e.position = position;
                e.salary = salary;
                logger.info("Upraven zaměstnanec: " + id);
                System.out.println("Zaměstnanec upraven.");
                return;
            }
        }
        System.out.println("Zaměstnanec s tímto ID nenalezen.");
    }

    public void removeEmployee(int id) {
        employees.removeIf(e -> e.id == id);
        logger.info("Odstraněn zaměstnanec: " + id);
        System.out.println("Zaměstnanec odstraněn.");
    }

    public Employee findEmployee(int id) {
        for (Employee e : employees) {
            if (e.id == id) return e;
        }
        return null;
    }

    public double getTotalSalaryCost() {
        return employees.stream().mapToDouble(e -> e.salary).sum();
    }

    public void printAllEmployees() {
        if (employees.isEmpty()) {
            System.out.println("Žádní zaměstnanci.");
        } else {
            employees.forEach(System.out::println);
        }
    }
}

class Order {
    int id;
    String title, description, status, receivedDate, dueDate;

    Order(int id, String title, String description, String status, String receivedDate, String dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.receivedDate = receivedDate;
        this.dueDate = dueDate;
    }

    public boolean isActive() {
        return !status.equalsIgnoreCase("Dokončena");
    }

    public String toString() {
        return id + ": " + title + " - " + status;
    }
}

class OrderManager {
    private List<Order> orders = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(OrderManager.class.getName());

    public void addOrder(int id, String title, String description, String status, String receivedDate, String dueDate) {
        orders.add(new Order(id, title, description, status, receivedDate, dueDate));
        logger.info("Přidána zakázka: " + id);
    }

    public void updateOrderStatus(int id, String newStatus) {
        for (Order o : orders) {
            if (o.id == id) {
                o.status = newStatus;
                logger.info("Aktualizován stav zakázky: " + id);
                System.out.println("Stav zakázky změněn.");
                return;
            }
        }
        System.out.println("Zakázka s tímto ID nenalezena.");
    }

    public void printActiveOrders() {
        orders.stream().filter(Order::isActive).forEach(System.out::println);
    }
}

class InventoryItem {
    int id, quantity, minimumStock;
    String name;

    InventoryItem(int id, String name, int quantity, int minimumStock) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.minimumStock = minimumStock;
    }

    public String toString() {
        return id + ": " + name + " - " + quantity + " ks";
    }
}

class InventoryManager {
    private Map<Integer, InventoryItem> items = new HashMap<>();
    private static final Logger logger = Logger.getLogger(InventoryManager.class.getName());

    public void addItem(int id, String name, int quantity, int minStock) {
        items.put(id, new InventoryItem(id, name, quantity, minStock));
        logger.info("Přidána položka: " + id);
    }

    public void updateQuantity(int id, int change) {
        InventoryItem item = items.get(id);
        if (item != null) {
            item.quantity += change;
            logger.info("Aktualizace skladu: " + id + ", nový stav: " + item.quantity);
        }
    }

    public boolean isAvailable(int id, int required) {
        InventoryItem item = items.get(id);
        return item != null && item.quantity >= required;
    }

    public void checkLowStock() {
        System.out.println("Položky pod minimem:");
        for (InventoryItem item : items.values()) {
            if (item.quantity < item.minimumStock) {
                System.out.println(item);
            }
        }
    }

    public void deductStock(int id, int amount) {
        InventoryItem item = items.get(id);
        if (item != null) {
            item.quantity -= amount;
        }
    }

    public void printAllItems() {
        for (InventoryItem item : items.values()) {
            System.out.println(item);
        }
    }
}

class OrderProcessing {
    private InventoryManager inventoryManager;
    private static final Logger logger = Logger.getLogger(OrderProcessing.class.getName());

    OrderProcessing(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public void createOrder(int itemId, int quantity) {
        if (inventoryManager.isAvailable(itemId, quantity)) {
            inventoryManager.deductStock(itemId, quantity);
            logger.info("Objednávka vyřízena: Položka " + itemId + ", množství: " + quantity);
            System.out.println("Objednávka úspěšně vyřízena.");
        } else {
            logger.warning("Nedostatek zásob pro položku: " + itemId);
            System.out.println("Objednávku nelze vyřídit – nedostatek zásob.");
        }
    }
}

public class CompanySystem {
    private static CompanySystem instance;
    public EmployeeManager employeeManager;
    public OrderManager orderManager;
    public InventoryManager inventoryManager;
    public OrderProcessing orderProcessing;

    private static final Logger logger = Logger.getLogger(CompanySystem.class.getName());

    private CompanySystem() {
        employeeManager = new EmployeeManager();
        orderManager = new OrderManager();
        inventoryManager = new InventoryManager();
        orderProcessing = new OrderProcessing(inventoryManager);
    }

    public static CompanySystem getInstance() {
        if (instance == null) {
            instance = new CompanySystem();
            logger.info("CompanySystem instance created.");
        }
        return instance;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CompanySystem cs = CompanySystem.getInstance();
        while (true) {
            System.out.println("\n1. Přidat zaměstnance\n2. Upravit zaměstnance\n3. Zobrazit všechny zaměstnance\n4. Mzdové náklady\n5. Přidat zakázku\n6. Změnit stav zakázky\n7. Zobrazit aktivní zakázky\n8. Přidat položku do skladu\n9. Odečíst položku ze skladu\n10. Zobrazit všechny položky\n11. Zkontrolovat nízké zásoby\n12. Vytvořit objednávku\n0. Konec");
            System.out.print("Zadej volbu: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Jméno: "); String j = scanner.nextLine();
                    System.out.print("Příjmení: "); String p = scanner.nextLine();
                    System.out.print("ID: "); int id = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Pozice: "); String poz = scanner.nextLine();
                    System.out.print("Plat: "); double plat = scanner.nextDouble(); scanner.nextLine();
                    cs.employeeManager.addEmployee(j, p, id, poz, plat);
                }
                case 2 -> {
                    System.out.print("ID zaměstnance: "); int id = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Nová pozice: "); String poz = scanner.nextLine();
                    System.out.print("Nový plat: "); double plat = scanner.nextDouble(); scanner.nextLine();
                    cs.employeeManager.editEmployee(id, poz, plat);
                }
                case 3 -> cs.employeeManager.printAllEmployees();
                case 4 -> System.out.println("Celkové náklady: " + cs.employeeManager.getTotalSalaryCost());
                case 5 -> {
                    System.out.print("ID: "); int id = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Název: "); String title = scanner.nextLine();
                    System.out.print("Popis: "); String desc = scanner.nextLine();
                    System.out.print("Stav: "); String stat = scanner.nextLine();
                    System.out.print("Datum přijetí: "); String dat1 = scanner.nextLine();
                    System.out.print("Termín: "); String dat2 = scanner.nextLine();
                    cs.orderManager.addOrder(id, title, desc, stat, dat1, dat2);
                }
                case 6 -> {
                    System.out.print("ID zakázky: "); int id = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Nový stav: "); String stat = scanner.nextLine();
                    cs.orderManager.updateOrderStatus(id, stat);
                }
                case 7 -> cs.orderManager.printActiveOrders();
                case 8 -> {
                    System.out.print("ID: "); int id = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Název: "); String name = scanner.nextLine();
                    System.out.print("Množství: "); int qty = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Minimum: "); int min = scanner.nextInt(); scanner.nextLine();
                    cs.inventoryManager.addItem(id, name, qty, min);
                }
                case 9 -> {
                    System.out.print("ID položky: "); int id = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Odebrat počet: "); int qty = scanner.nextInt(); scanner.nextLine();
                    cs.inventoryManager.updateQuantity(id, -qty);
                }
                case 10 -> cs.inventoryManager.printAllItems();
                case 11 -> cs.inventoryManager.checkLowStock();
                case 12 -> {
                    System.out.print("ID položky: "); int id = scanner.nextInt(); scanner.nextLine();
                    System.out.print("Počet: "); int qty = scanner.nextInt(); scanner.nextLine();
                    cs.orderProcessing.createOrder(id, qty);
                }
                case 0 -> {
                    System.out.println("Konec programu.");
                    return;
                }
                default -> System.out.println("Neplatná volba.");
            }
        }
    }
}
