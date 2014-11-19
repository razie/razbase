/**
 * Razvan's public code. 
 * Copyright 2008 based on Apache license (share alike) see LICENSE.txt for details.
 */
package razie.draw;


/**
 * defines the contract for a streamable container - a container that can be attached to a stream
 * 
 * <p>
 * The protocol for writing into a stream is: the stream will notify you when it's your turn: you're
 * open. At that point you write whatever you have and you effectively suspend the stream. you must
 * release the stream when you're done, however.
 * 
 * <ul>
 * <li>this is created with or wihtout a stream. if in a stream, will write header.
 * <li>as elements are added and one "unit" complete, will write to the stream
 * <li>upon closing, will write the footer
 * </ul>
 * 
 * $
 * @author razvanc99
 * 
 */
public interface StreamableContainer extends DrawAccumulator {
    public DrawStream.ElementState getState();

    public void setState(DrawStream.ElementState state);

    public DrawStream getStream();

    public abstract static class Impl implements StreamableContainer {
        DrawStream.ElementState state         = DrawStream.ElementState.WAITING;
        DrawStream               ownerStream;
        boolean                  wroteHeader   = false;
        boolean                  wroteElements = false;
        boolean                  wroteFooter   = false;

        /**
         * streams calls this when object is opened...if you already had accumulated something, can
         * write it
         */

        void open(DrawStream owner) {
            if (this.ownerStream != null && this.ownerStream != owner) {
                throw new IllegalStateException("streamable already belongs to another stream...");
            } else if (this.ownerStream != null) {
                return; // avoid recursion
            }
            this.state = DrawStream.ElementState.OPEN;

            this.ownerStream = owner;
            // stream protects against recursive opening here :)
            owner.open(this);
            // if i can start writing, the owner will update my status to open and write
        }

        public void close() {
            if (this.ownerStream != null) {
                this.ownerStream.close(this);
            }
            this.state = DrawStream.ElementState.CLOSED;
        }

        public DrawStream.ElementState getState() {
            return this.state;
        }

        public void setState(DrawStream.ElementState state) {
            this.state = state;
        }

        public DrawStream getStream() {
            return this.ownerStream;
        }

    }
}
