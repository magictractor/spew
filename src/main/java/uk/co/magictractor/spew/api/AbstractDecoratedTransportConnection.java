package uk.co.magictractor.spew.api;

public abstract class AbstractDecoratedTransportConnection<APP extends SpewApplication, SP extends SpewServiceProvider>
        extends AbstractConnection<APP, SP> {

    public AbstractDecoratedTransportConnection(APP application) {
        super(application);
    }

}
