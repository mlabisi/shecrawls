#Author: Catherine Gronkiewicz
#Assignment: Project #5
#Completed: November 15, 2018


class ItemToPurchase: #ItemToPurchase class

    def __init__(self, name_item = "none", price_item = 0, quantity_item = 0, description_item = "none"): #default constructor
        self.name_item = name_item
        self.price_item = price_item
        self.quantity_item = quantity_item
        self.description_item = description_item

    def print_item_cost(self): #method that prints the cost of the item
        string = ("{} {} @ ${} = ${}".format(self.name_item, self.quantity_item, self.price_item, (self.quantity_item * self.price_item)))
        cost = (self.quantity_item * self.price_item)
        return string, cost

    def print_item_description(self): #method that prints the items' descriptions
        string = ("{}: {}".format(self.name_item, self.description_item)) #formats the output
        print(string, end='\n')
        return string

class ShoppingCart: #ShoppingCart class

    def __init__(self, customer_name = "none", current_date = "January 1, 2016", cart_items = []): #default constructor
        self.customer_name = customer_name
        self.current_date = current_date
        self.cart_items = cart_items

    def add_item(self, item): #asks for user input to add items to shopping cart
        print("\nADD ITEM TO CART", end="\n") #asks for user input 
        item_name = str(input("Enter the item name: \n")) #prompts for name and description of item, price, and quantity
        item_description = str(input("Enter the item description: \n"))
        item_price = int(input("Enter the item price: \n"))
        item_quantity = int(input("Enter the item quantity: \n"))
        
        self.cart_items.append(ItemToPurchase(item_name, item_price, item_quantity, item_description)) #appends added items to cart_items list

    def remove_item(self): #removes items from shopping cart list
        print("\nREMOVE ITEM FROM CART", end="\n")
        index = str(input("Enter name of item to remove: \n")) #prompts user what item to remove from cart
        i = 0
        for item in self.cart_items: #iterates through every item
            if(item.name_item == index):
                del self.cart_items[i] #deletes the item from list
                i += 1
                flag = True
                break
            else:
                flag = False
        if(flag == False):
            print("Item not found in cart. Nothing removed.") #prints message if item is not found in the list

    def modify_item(self): #changes the quantity of a specific item
        print("\nCHANGE ITEM QUANTITY", end="\n")
        name = str(input("Enter the item name: \n")) #prompts for user input of item
        for item in self.cart_items:
            if(item.name_item == name): #updates quantity of chosen item
                quantity = int(input("Enter the new quantity: \n"))
                item.quantity_item = quantity
                flag = True
                break
            else:
                flag = False
        if(flag == False):
            print("Item not found in cart. Nothing modified.") #prints message if item is not found

    def get_num_items_in_cart(self): #accumulates the total number of items in the shopping cart
        total_items = 0
        for item in self.cart_items:
            total_items = total_items + item.quantity_item #adds the quantities and number of items together
        return total_items

    def get_cost_of_cart(self): #get the total cost of the items in the shopping cart
        total_cost = 0
        cost = 0
        for item in self.cart_items: #iterates through each of the items
            cost = (item.quantity_item * item.price_item) #multiplies the quantity and number of items
            total_cost += cost
        return total_cost

    def print_total(): #method to print the total cost of the shopping cart items
        total_cost = get_cost_of_cart()
        if(total_cost == 0):
            print("SHOPPING CART IS EMPTY") #prints message if shopping cart is empty
        else:
            output_cart() #prints what is in the shopping cart list

    def print_descriptions(self): #prints the description of each of the items
        print("\nOUTPUT ITEMS\' DESCRIPTIONS")
        print("{}\'s Shopping Cart - {}".format(self.customer_name, self.current_date), end="\n") #string format for customer name and current date
        print("\nItem Descriptions", end="\n") #prints the description of item
        for item in self.cart_items: #iterates through each of the items
            print("{}: {}".format(item.name_item, item.description_item), end="\n")
            
    def output_cart(self): #displays the items in the shopping cart
        newOutput = ShoppingCart()
        print("\nOUTPUT SHOPPING CART", end="\n")
        print("{}\'s Shopping Cart - {}".format(self.customer_name, self.current_date), end="\n") #string format for shopping cart name and date
        print("Number of Items:", newOutput.get_num_items_in_cart(), end="\n")
        total_count = 0 #initial value of total items
        for item in self.cart_items:
            print("{} {} @ ${} = ${}".format(item.name_item, item.quantity_item, item.price_item, (item.quantity_item * item.price_item)), end="\n")
            total_count += (item.quantity_item * item.price_item) #accumulates the total count of items and their prices
        print("Total: ${}".format(total_count), end="\n")

def print_menu(ShoppingCart): #prints the menu for the shopping cart
    customerInput = newInput
    string = ""
    menu = ("\nMENU\n"
    "a - Add item to cart\n"
    "r - Remove item from cart\n"
    "c - Change item quantity\n"
    "i - Output items' description\n"
    "o - Output shopping cart\n"
    "q - Quit\n")
    userInput = "" #user input
    while(userInput != "q"):
        string=""
        print(menu, end="\n")
        userInput = input("Choose an option: ") #user chooses a menu option
        if(userInput == "a"): #user adds items to the shopping cart
            customerInput.add_item(string)
        if(userInput == "r"): #user removes items from the shopping cart
            customerInput.remove_item()
        if(userInput == "c"): #user modifies one of the items from the shopping cart
            customerInput.modify_item()
        if(userInput == "i"): #user outputs items' descriptions
            customerInput.print_descriptions()
        if(userInput == "o"): #user outputs shopping cart
            customerInput.output_cart()

customer_name = str(input("Enter customer\'s name: \n")) #prompts for customer name
current_date = str(input("Enter today\'s date: \n")) #prompts for the current date
print("Customer name:", customer_name, end="\n") #prints customer name
print("Today\'s date:", current_date, end="\n") #prints current date
newInput = ShoppingCart(customer_name, current_date) #calls the ShoppingCart class with the parameters
print_menu(newInput) #prints menu


#Outputs
'''
Enter customer's name: 
John Doe
Enter today's date: 
March 19, 2019
Customer name: John Doe
Today's date: March 19, 2019

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: a

ADD ITEM TO CART
Enter the item name: 
Nike Romaleos
Enter the item description: 
Volt color, Weightlifting shoes
Enter the item price: 
189
Enter the item quantity: 
2

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: a

ADD ITEM TO CART
Enter the item name: 
Apples
Enter the item description: 
Red Gala Apples
Enter the item price: 
10
Enter the item quantity: 
5

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: a

ADD ITEM TO CART
Enter the item name: 
Chocolate Chips
Enter the item description: 
Semi-sweet
Enter the item price: 
3
Enter the item quantity: 
15

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: a

ADD ITEM TO CART
Enter the item name: 
Powerbeats
Enter the item description: 
2 Headphones
Enter the item price: 
128
Enter the item quantity: 
1

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: r

REMOVE ITEM FROM CART
Enter name of item to remove: 
Chocolate Chips

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: c

CHANGE ITEM QUANTITY
Enter the item name: 
Powerbeats
Enter the new quantity: 
5

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: i

OUTPUT ITEMS' DESCRIPTIONS
John Doe's Shopping Cart - March 19, 2019

Item Descriptions
Apples: Red Gala Apples
Chocolate Chips: Semi-sweet
Powerbeats: 2 Headphones

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: o

OUTPUT SHOPPING CART
John Doe's Shopping Cart - March 19, 2019
Number of Items: 25
Apples 5 @ $10 = $50
Chocolate Chips 15 @ $3 = $45
Powerbeats 5 @ $128 = $640
Total: $735

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: q
'''

'''
Enter customer's name: 
Billy Bronco
Enter today's date: 
January 4, 2021
Customer name: Billy Bronco
Today's date: January 4, 2021

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: a

ADD ITEM TO CART
Enter the item name: 
Sunglasses
Enter the item description: 
Ray Bann Sunglasses
Enter the item price: 
234
Enter the item quantity: 
5

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: a

ADD ITEM TO CART
Enter the item name: 
Water Bottle
Enter the item description: 
HydroFlask Bottle
Enter the item price: 
85
Enter the item quantity: 
15

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: r

REMOVE ITEM FROM CART
Enter name of item to remove: 
Sunglasses

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: c

CHANGE ITEM QUANTITY
Enter the item name: 
Water Bottle
Enter the new quantity: 
25

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: i

OUTPUT ITEMS' DESCRIPTIONS
Billy Bronco's Shopping Cart - January 4, 2021

Item Descriptions
Water Bottle: HydroFlask Bottle

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: o

OUTPUT SHOPPING CART
Billy Bronco's Shopping Cart - January 4, 2021
Number of Items: 25
Water Bottle 25 @ $85 = $2125
Total: $2125

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: q
'''

'''
Enter customer's name: 
Cathy Gronkiewicz
Enter today's date: 
November 17, 2018
Customer name: Cathy Gronkiewicz
Today's date: November 17, 2018

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: a

ADD ITEM TO CART
Enter the item name: 
Books
Enter the item description: 
Scary Books
Enter the item price: 
15
Enter the item quantity: 
8

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: r

REMOVE ITEM FROM CART
Enter name of item to remove: 
Apples
Item not found in cart. Nothing removed.

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: c

CHANGE ITEM QUANTITY
Enter the item name: 
Puzzle
Item not found in cart. Nothing modified.

MENU
a - Add item to cart
r - Remove item from cart
c - Change item quantity
i - Output items' description
o - Output shopping cart
q - Quit

Choose an option: q
'''


    
