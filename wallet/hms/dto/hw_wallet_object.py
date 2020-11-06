# -*- coding: utf-8 -*- 
# @Author    :
# @Date      :2020/6/9
# @Version   :Python 3.8.3
# @File      :hw_wallet_object


class HwWalletObject(object):
    """
      HwWalletObject类
    """

    def __init__(self):
        self.passVersion = None
        self.passTypeIdentifier = None
        self.passStyleIdentifier = None
        self.organizationName = None
        self.organizationPassId = None
        self.serialNumber = None
        self.fields = None
        self.linkDevicePass = None

    def set_passVersion(self, passVersion):
        self.passVersion = passVersion

    def get_passVersion(self):
        return self.passVersion

    def set_passTypeIdentifier(self, passTypeIdentifier):
        self.passTypeIdentifier = passTypeIdentifier

    def get_passTypeIdentifier(self):
        return self.passTypeIdentifier

    def set_passStyleIdentifier(self, passStyleIdentifier):
        self.passStyleIdentifier = passStyleIdentifier

    def get_passStyleIdentifier(self):
        return self.passStyleIdentifier

    def set_organizationName(self, organizationName):
        self.organizationName = organizationName

    def get_organizationName(self):
        return self.organizationName

    def set_organizationPassId(self, organizationPassId):
        self.organizationPassId = organizationPassId

    def get_organizationPassId(self):
        return self.organizationPassId

    def set_serialNumber(self, serialNumber):
        self.serialNumber = serialNumber

    def get_serialNumber(self):
        return self.serialNumber

    def set_fields(self, fields):
        self.fields = fields

    def get_fields(self):
        return self.fields

    def set_linkDevicePass(self, linkDevicePass):
        self.linkDevicePass = linkDevicePass

    def get_linkDevicePass(self):
        return self.linkDevicePass
