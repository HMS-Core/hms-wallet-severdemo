# -*- coding: utf-8 -*- 
# @Author    :
# @Date      :2020/6/11
# @Version   :Python 3.8.3
# @File      :batch_get_hw_wallet_result


class BatchGetHwWalletResult(object):
    def __init__(self):
        # PageInfo object
        self.pageInfo = None
        # List<HwWalletObject>
        self.data = None

    def set_pageInfo(self, pageInfo):
        self.pageInfo = pageInfo

    def get_pageInfo(self):
        return self.pageInfo

    def set_data(self, data):
        self.data = data

    def get_data(self):
        return self.data
