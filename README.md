## Relevant Documentation
* [Pureconfig](https://pureconfig.github.io/docs/)
* [Cats Effect 3](https://typelevel.org/cats-effect/docs/getting-started)
___

To run an individual test:
```bash
sbt 'testOnly *ConfigSpec -- -z "should error when the config is invalid"'
```

---