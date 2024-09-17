package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteAddressMap;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteOverlay;

import java.util.Map;
import java.util.Set;

public class ArmContext {
    ParagonLiteOverlay overlay;
    byte[] data;
    int initialAddress;
    int currentAddress;
    Map<Integer, Integer> dataValues;
    Set<Integer> labelAddresses;
    ParagonLiteAddressMap addressMap;
    
    public ArmContext(ParagonLiteOverlay overlay, byte[] data, int initialAddress, int currentAddress, Map<Integer, Integer> dataValues, Set<Integer> labelAddresses, ParagonLiteAddressMap addressMap) {
        this.overlay = overlay;
        this.data = data;
        this.initialAddress = initialAddress;
        this.currentAddress = currentAddress;
        this.dataValues = dataValues;
        this.labelAddresses = labelAddresses;
        this.addressMap = addressMap;
    }
    
    public int getInitialAddress() {
        return initialAddress;
    }
    
    public int getCurrentAddress() {
        return currentAddress;
    }
    
    public int getDataAtAddress(int address) {
        address = address & 0xFFFFFFFC;
        
        if (!dataValues.containsKey(address)) {
            int offset = address - initialAddress;
            int value = (FileFunctions.readFullInt(data, offset));
            dataValues.put(address, value);
        }
        
        return dataValues.get(address);
    }
    
    public void setDataAtAddress(int address, int value) {
        address = address & 0xFFFFFFFC;
        dataValues.put(address, value);
    }
    
    public void addLabelAddress(int address) {
        labelAddresses.add(address);
    }
    
    public int getFuncEncoding(int ramAddress) {
        int defaultEncoding = 2;
        
        ParagonLiteOverlay overlay = addressMap.findOverlay(ramAddress, this.overlay);
        if (overlay == null)
            return defaultEncoding;

        ParagonLiteAddressMap.AddressBase addressBase = addressMap.getAddressData(overlay, ramAddress);
        if (addressBase == null)
            return defaultEncoding;

        if (addressBase instanceof ParagonLiteAddressMap.CodeAddress codeAddress)
            return codeAddress.getEncoding();
        
        return defaultEncoding;
    }
    
    public String getFuncName(int ramAddress) {
        ParagonLiteOverlay overlay = addressMap.findOverlay(ramAddress, this.overlay);
        if (overlay == null)
            return getUnknownFuncName(ramAddress);
        
        ParagonLiteAddressMap.AddressBase addressBase = addressMap.getAddressData(overlay, ramAddress);
        if (addressBase == null)
            return getUnknownFuncName(ramAddress);
        
        if (addressBase instanceof ParagonLiteAddressMap.LabeledAddressInterface labeledAddress)
            return String.format("%s::%s", overlay, labeledAddress.getLabel());
        
        return addressBase.toString();
    }
    
    private String getUnknownFuncName(int ramAddress) {
        return String.format("Unk_%X", ramAddress);
    }
}
