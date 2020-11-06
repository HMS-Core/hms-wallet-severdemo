# -*- coding: utf-8 -*-
# @Author    :
# @Date      :2020/6/9
# @Version   :Python 3.8.3
# @File      :hw_wallet_object


class Fields(object):
    """
      Field class
    """

    def __init__(self):
        self.srcPassTypeIdentifier = None
        self.srcPassTypeIdentifier = None
        self.srcPassIdentifier = None
        self.countryCode = None
        self.isUserDiy = None
        self.status = None
        self.relatedPassIds = None
        self.locationList = None
        self.barCode = None
        self.commonFields = None
        self.appendFields = None
        self.messageList = None
        self.timeList = None
        self.imageList = None
        self.textList = None
        self.localized = None
        self.ticketInfoList = None

    def set_srcPassTypeIdentifier(self, srcPassTypeIdentifier):
        self.srcPassTypeIdentifier = srcPassTypeIdentifier

    def get_srcPassTypeIdentifier(self):
        return self.srcPassTypeIdentifier

    def set_srcPassIdentifier(self, srcPassIdentifier):
        self.srcPassIdentifier = srcPassIdentifier

    def get_srcPassIdentifier(self):
        return self.srcPassIdentifier

    def set_countryCode(self, countryCode):
        self.countryCode = countryCode

    def get_countryCode(self):
        return self.countryCode

    def set_isUserDiy(self, isUserDiy):
        self.isUserDiy = isUserDiy

    def get_isUserDiy(self):
        return self.isUserDiy

    def set_status(self, status):
        self.status = status

    def get_status(self):
        return self.status

    def set_relatedPassIds(self, relatedPassIds):
        self.relatedPassIds = relatedPassIds

    def get_relatedPassIds(self):
        return self.relatedPassIds

    def set_locationList(self, locationList):
        self.locationList = locationList

    def get_locationList(self):
        return self.locationList

    def set_barCode(self, barCode):
        self.barCode = barCode

    def get_barCode(self):
        return self.barCode

    def set_commonFields(self, commonFields):
        self.commonFields = commonFields

    def get_commonFields(self):
        return self.commonFields

    def set_appendFields(self, appendFields):
        self.appendFields = appendFields

    def get_appendFields(self):
        return self.appendFields

    def set_messageList(self, messageList):
        self.messageList = messageList

    def get_messageList(self):
        return self.messageList

    def set_timeList(self, timeList):
        self.timeList = timeList

    def get_timeList(self):
        return self.timeList

    def set_imageList(self, imageList):
        self.imageList = imageList

    def get_imageList(self):
        return self.imageList

    def set_textList(self, textList):
        self.textList = textList

    def get_textList(self):
        return self.textList

    def set_localized(self, localized):
        self.localized = localized

    def get_localized(self):
        return self.localized

    def set_ticketInfoList(self, ticketInfoList):
        self.ticketInfoList = ticketInfoList

    def get_ticketInfoList(self):
        return self.ticketInfoList


