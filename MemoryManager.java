import java.nio.*;
import java.lang.Integer;
import java.math.BigInteger;

public class MemoryManager
{
    private byte[] allocatedMemoryPool;
    
    public MemoryManager() {}

    public boolean allocateMemory(int allocAmount)
    {
        if(allocatedMemoryPool == null || allocatedMemoryPool.length == 0)
        {
            allocatedMemoryPool = new byte[allocAmount];
            return true;
        }
        else
        {
            System.out.println("[MemoryManager] Exception Occured: Failed to allocate memory as it is currently in use.");
            return false;
        }
    }

    public boolean freeMemory()
    {
        if(allocatedMemoryPool != null || allocatedMemoryPool.length != 0)
        {
            //properly free the allocated memory
            for(int memByteIndex = 0; memByteIndex < allocatedMemoryPool.length; memByteIndex++)
                allocatedMemoryPool[memByteIndex] = 0x0;

            //gracefully deallocate the empty resource that is not needed anymore in the "actual" memory
            allocatedMemoryPool = null;
            return true;
        }
        else
        {
            System.out.println("[MemoryManager] Exception Occured: Failed to free memory as no memory is being occupied.");
            return false;
        }
    }
    
    public MemoryProperties writeMemory(Object memoryValueToWrite)
    {
        Object typeOfMemValClass = memoryValueToWrite.getClass();
        byte[] convertedMemoryValueBytes;

        //START VALUE TO BYTE[] CONVERSION
        if(typeOfMemValClass == Integer.class)
            convertedMemoryValueBytes = BigInteger.valueOf((int)memoryValueToWrite).toByteArray();
        else if(typeOfMemValClass == String.class)
        {
            char[] allChars = ((String)memoryValueToWrite).toCharArray();
            int lengthOfChar = allChars.length;

            convertedMemoryValueBytes = new byte[lengthOfChar];

            for(int charIndex = 0; charIndex < lengthOfChar; charIndex++)
                convertedMemoryValueBytes[charIndex] = (byte)(allChars[charIndex]);
        }
        else if(typeOfMemValClass == Double.class)
        {
            convertedMemoryValueBytes = new byte[8]; //pre allocate 8 bytes as double is a 8 bytes data type
            long lngBits = Double.doubleToLongBits((double)memoryValueToWrite);

            for(int i = 0; i < 8; i++) 
                convertedMemoryValueBytes[i] = (byte)((lngBits >> ((7 - i) * 8)) & 0xff);
        }
        else
        {
            System.out.println("[MemoryManager] Exception Occured: Failed to write memory as the value being written is not a support data type at the time of implementation.");
            return new MemoryProperties(0, 0);
        }
        //END VALUE TO BYTE[] CONVERSION
        
        //START WRITE CONVERTED BYTE[] TO MEMORY POOL
        int indexOfEmptyMemoryAddress = getEmptyAddressIndex();

        if(convertedMemoryValueBytes.length > allocatedMemoryPool.length || (allocatedMemoryPool[indexOfEmptyMemoryAddress] + convertedMemoryValueBytes.length) > allocatedMemoryPool.length
        || indexOfEmptyMemoryAddress == -1 )
        {
            System.out.println("[MemoryManager] Exception Occured: Failed to write memory due to insufficient memory allocation.");
            return new MemoryProperties(0, 0);
        }
        else
        {
            for(int currentMemBytesIndex = 0; currentMemBytesIndex < convertedMemoryValueBytes.length; currentMemBytesIndex++)
                allocatedMemoryPool[indexOfEmptyMemoryAddress + currentMemBytesIndex] = convertedMemoryValueBytes[currentMemBytesIndex];

            return new MemoryProperties(indexOfEmptyMemoryAddress, convertedMemoryValueBytes.length);
        }
        //END WRITE CONVERTED BYTE[] TO MEMORY POOL
    }

    public Object readMemory(int indexToReadFrom, int amountToRead, Object returnObject)
    {
        if(indexToReadFrom == -1 || allocatedMemoryPool[indexToReadFrom] == 0x0)
        {
            System.out.println("[MemoryManager] Exception Occured: Failed to read memory at 0x" + indexToReadFrom + " due to an invalid memory access.");
            returnObject = null; 
        }
        else
        {
            byte[] readBytes = new byte[amountToRead];

            //START READ REQUIRED BYTES FROM THE MEMORY POOL
            for(int byteIndex = 0; byteIndex <= (amountToRead - 1); byteIndex++)
                readBytes[byteIndex] = allocatedMemoryPool[indexToReadFrom + byteIndex];
            //END READ REQUIRED BYTES FROM THE MEMORY POOL

            //START CONVERSION OF BYTES READ INTO RETURNOBJECT TYPE
            if(returnObject.getClass() == Integer.class)
            {
                for(int byteIndex = 0; byteIndex < readBytes.length; byteIndex++)
                    returnObject = ((int)returnObject << 8) + (readBytes[byteIndex] & 0xFF); //left shift the bytes
            }
            else if(returnObject.getClass() == String.class)
            {
                char[] characters = new char[readBytes.length];

                for(int byteIndex = 0; byteIndex < readBytes.length; byteIndex++)
                    characters[byteIndex] = (char)readBytes[byteIndex];

                returnObject = new String(characters);
            }
            else if(returnObject.getClass() == Double.class)
                returnObject = ByteBuffer.wrap(readBytes).getDouble(0);
            else
            {
                System.out.println("[MemoryManager] Exception Occured: Failed to read memory at 0x" + indexToReadFrom + " due to an unsupported data type.");
                returnObject = null;
            }
            //END CONVERSION OF BYTES READ INTO RETURNOBJECT TYPE
        }

        return returnObject;
    }
    
    private int getEmptyAddressIndex()
    {
        for(int addrIndex = 0; addrIndex < allocatedMemoryPool.length; addrIndex++)
            if(allocatedMemoryPool[addrIndex] == 0x0)
                return addrIndex;
                
        return -1;
    }

    public class MemoryProperties
    {
        public MemoryProperties(int strIndex, int numOfBytesWritten)
        {
            startIndex = strIndex;
            numberOfBytesWritten = numOfBytesWritten;
        }

        public int startIndex;
        public int numberOfBytesWritten;
    }
}
