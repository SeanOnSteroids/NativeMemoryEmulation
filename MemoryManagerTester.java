
public class MemoryManagerTester
{
    public MemoryManagerTester()
    {
        MemoryManager memMgr = new MemoryManager();
        
        boolean allocRes = memMgr.allocateMemory(1000); //allocate 1000 bytes
        
        if(allocRes)
            System.out.println("Successfully allocated 1000 bytes");

        testReadWriteMemoryInt(memMgr);
        testReadWriteMemoryString(memMgr);
        testReadWriteMemoryDouble(memMgr);

        if(memMgr.freeMemory())
             System.out.println("Successfully free'ed the allocated memory");
    }

    private void testReadWriteMemoryInt(MemoryManager memMgr)
    {
        MemoryManager.MemoryProperties memProp = memMgr.writeMemory(1000);

        System.out.println("Wrote " + memProp.numberOfBytesWritten + " bytes at memory address: 0x" + memProp.startIndex + " for an int value of 1000");

        int retValType = 0;
        int retVal = (int)memMgr.readMemory(memProp.startIndex, memProp.numberOfBytesWritten, retValType);

        System.out.println("return value: " + retVal);
    }

    private void testReadWriteMemoryString(MemoryManager memMgr)
    {
        MemoryManager.MemoryProperties memProp = memMgr.writeMemory("Hello World!");

        System.out.println("Wrote " + memProp.numberOfBytesWritten + " bytes at memory address: 0x" + memProp.startIndex+ " for a string value of [Hello World!]");

        String retValType = "";
        String retVal = (String)memMgr.readMemory(memProp.startIndex, memProp.numberOfBytesWritten, retValType);

        System.out.println("return value: " + retVal);
    }

    private void testReadWriteMemoryDouble(MemoryManager memMgr)
    {
        MemoryManager.MemoryProperties memProp = memMgr.writeMemory(10.12);

        System.out.println("Wrote " + memProp.numberOfBytesWritten + " bytes at memory address: 0x" + memProp.startIndex + " for a double value of 10.12");

        double retValType = 0.0;
        double retVal = (double)memMgr.readMemory(memProp.startIndex, memProp.numberOfBytesWritten, retValType);

        System.out.println("return value: " + retVal);
    }
}
