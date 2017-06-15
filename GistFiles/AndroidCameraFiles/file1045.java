class Customer {
	public String id;
	public Address address;
	public Phone phone;
	public String email;
	public Account account;
}

class Account{
	public String id;
	public Address billing_address;
	public boolean is_closed;
	public Date open;
	public Date closed;
}