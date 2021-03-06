# -*- coding: utf-8 -*-
import tweepy
import os
import json
import argparse

consumer_key = "q1BbMVgJGpPZ3IVrCkmv1tcix"# os.environ['TWITTER_CONSUMER_KEY']
consumer_secret = "sfwpBjHPijxbjTBDyiOmFb5MxcGa3LaXUpim1NmbA1gWFfzVjK"#os.environ['TWITTER_CONSUMER_SECRET']
access_key = "770679954-vWDVGhFKOdwR1vzN56Cgcc2Rcf4QoGhCBPwOvcgd"#os.environ['TWITTER_ACCESS_KEY']
access_secret = "VKmuoM3wLDwT8QrOhyzE9ySIsZQnnEp4FSRGdXVLbTLSl"#os.environ['TWITTER_ACCESS_SECRET']


class EchoStreamListener(tweepy.StreamListener):
    def __init__(self, api, dump_json=False, numtweets=0):
        self.api = api
        self.dump_json = dump_json
        self.count = 0
        self.limit = int(numtweets)
        super(tweepy.StreamListener, self).__init__()

    def on_data(self, tweet):
        tweet_data = json.loads(tweet)
        if 'text' in tweet_data:
            if self.dump_json:
                print(tweet.rstrip())
            else:
                print(tweet_data['text'].encode("utf-8").rstrip())

            self.count = self.count+1
            return False if self.count == self.limit else True

    def on_error(self, status_code):
        return True

    def on_timeout(self):
        return True


def get_parser():
    parser = argparse.ArgumentParser(add_help=True)

    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument(
        '-j', '--json',
        action='store_true',
        help='dump each tweet as a json string'
    )
    group.add_argument(
        '-t', '--text',
        dest='json',
        action='store_false',
        help='dump each tweet\'s text'
    )
    parser.add_argument(
        '-n', '--numtweets',
        metavar='numtweets',
        help='set number of tweets to retrieve'
    )
    return parser


if __name__ == '__main__':
    parser = get_parser()
    args = parser.parse_args()

    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_key, access_secret)
    api = tweepy.API(auth)
    sapi = tweepy.streaming.Stream(
        auth, EchoStreamListener(
            api=api, dump_json=args.json, numtweets=args.numtweets))
    sapi.sample()
