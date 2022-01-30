package Controllers;

import services.RestClient;

public abstract class Controller {

    protected RestClient restClient;
    protected final String Base_URL = "https://jsonplaceholder.typicode.com";

    public Controller()
    {
        restClient = new RestClient(Base_URL);
    }


}
